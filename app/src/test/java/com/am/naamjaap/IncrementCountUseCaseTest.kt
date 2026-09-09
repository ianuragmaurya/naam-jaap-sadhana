package com.am.naamjaap

import com.am.naamjaap.domain.model.DailyStat
import com.am.naamjaap.domain.repository.DailyStatRepository
import com.am.naamjaap.domain.repository.MantraProfileRepository
import com.am.naamjaap.domain.usecase.IncrementCountUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class IncrementCountUseCaseTest {

    private lateinit var mantraProfileRepository: MantraProfileRepository
    private lateinit var dailyStatRepository: DailyStatRepository
    private lateinit var incrementCountUseCase: IncrementCountUseCase

    @Before
    fun setUp() {
        mantraProfileRepository = mockk(relaxed = true)
        dailyStatRepository = mockk(relaxed = true)
        incrementCountUseCase = IncrementCountUseCase(mantraProfileRepository, dailyStatRepository)
    }

    @Test
    fun `incrementing count below target does not complete mala`() = runTest {
        // ARRANGE — koi purana stat nahi hai aaj ke liye (fresh day)
        coEvery { dailyStatRepository.getForDateAndProfile(any(), any()) } returns null

        // ACT — mala target 108 hai, abhi count 5 hai, tap karne pe 6 hoga (108 se bahut door)
        val result = incrementCountUseCase(profileId = 1L, currentCount = 5, malaTarget = 108)

        // ASSERT
        assertEquals(6, result.newCount)
        assertFalse(result.malaJustCompleted)
    }

    @Test
    fun `incrementing count to exactly target completes mala`() = runTest {
        coEvery { dailyStatRepository.getForDateAndProfile(any(), any()) } returns null

        // ACT — count 107 hai, tap karne pe 108 (== malaTarget) ho jayega
        val result = incrementCountUseCase(profileId = 1L, currentCount = 107, malaTarget = 108)

        assertEquals(108, result.newCount)
        assertTrue(result.malaJustCompleted)
        assertEquals(1, result.totalMalasToday)
    }

    @Test
    fun `increment always updates lifetime count in repository`() = runTest {
        coEvery { dailyStatRepository.getForDateAndProfile(any(), any()) } returns null

        incrementCountUseCase(profileId = 42L, currentCount = 0, malaTarget = 108)

        // VERIFY — check karo ki incrementLifetimeCount function
        // profileId=42 aur increment=1 ke saath call hua tha
        coVerify { mantraProfileRepository.incrementLifetimeCount(42L, 1) }
    }

    @Test
    fun `mala completion count accumulates from existing daily stat`() = runTest {
        // ARRANGE — aaj already 2 mala complete ho chuki hain
        coEvery { dailyStatRepository.getForDateAndProfile(any(), any()) } returns DailyStat(
            dateEpochDay = 0L,
            mantraProfileId = 1L,
            totalCount = 216,   // 2 malas ka 108*2
            malaCompletions = 2
        )

        // ACT — teesri mala complete hoti hai (107 → 108)
        val result = incrementCountUseCase(profileId = 1L, currentCount = 107, malaTarget = 108)

        // ASSERT — ab totalMalasToday 3 hona chahiye (2 purani + 1 nayi)
        assertEquals(3, result.totalMalasToday)
    }
}