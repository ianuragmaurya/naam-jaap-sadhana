package com.am.naamjaap

import com.am.naamjaap.domain.repository.DailyStatRepository
import com.am.naamjaap.domain.repository.JapaSessionRepository
import com.am.naamjaap.domain.usecase.GetStreakUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetStreakUseCaseTest {

    // "lateinit var" — ye variable baad mein (setUp function mein) initialize hoga,
    // abhi sirf declare kar rahe hain.
    private lateinit var dailyStatRepository: DailyStatRepository
    private lateinit var getStreakUseCase: GetStreakUseCase

    @Before
    fun setUp() {
        // mockk() — ek "fake" JapaSessionRepository banata hai automatically.
        // Ye interface ko implement karta hai, lekin abhi kuch bhi nahi karta
        // jab tak hum specifically na batayein "jab ye function call ho, ye return karo".
        dailyStatRepository = mockk()
        getStreakUseCase = GetStreakUseCase(dailyStatRepository = dailyStatRepository)
    }

    @Test
    fun `streak is zero when there are no sessions`() = runTest {
        // ARRANGE — fake repository ko batao: jab getAllSessionDates() call ho, empty list do
        coEvery { dailyStatRepository.getAllActiveDates() } returns emptyList()

        // ACT
        val streak = getStreakUseCase()

        // ASSERT
        assertEquals(0, streak)
    }

    @Test
    fun `streak counts consecutive days ending today`() = runTest {
        val today = LocalDate.now().toEpochDay()
        // ARRANGE — pichle 3 din consecutive chanting hui hai (aaj, kal, parso)
        coEvery { dailyStatRepository.getAllActiveDates() } returns listOf(
            today, today - 1, today - 2
        )

        val streak = getStreakUseCase()

        assertEquals(3, streak)
    }

    @Test
    fun `streak breaks when a day is missing`() = runTest {
        val today = LocalDate.now().toEpochDay()
        // ARRANGE — aaj aur parso chanting hui, lekin KAL nahi hui (gap hai)
        coEvery { dailyStatRepository.getAllActiveDates() } returns listOf(
            today, today - 2
        )

        val streak = getStreakUseCase()

        // Sirf "today" count hoga, kyunki kal ka gap streak tod deta hai
        assertEquals(1, streak)
    }
}