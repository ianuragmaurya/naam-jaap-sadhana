package com.am.naamjaap

import android.content.Context
import com.am.naamjaap.data.local.datastore.UserPreferences
import com.am.naamjaap.domain.model.MantraProfile
import com.am.naamjaap.domain.repository.MantraProfileRepository
import com.am.naamjaap.domain.repository.UserPreferencesRepository
import com.am.naamjaap.domain.usecase.GetTodayStatsUseCase
import com.am.naamjaap.domain.usecase.IncrementCountUseCase
import com.am.naamjaap.domain.usecase.IncrementResult
import com.am.naamjaap.presentation.counter.CounterViewModel
import com.am.naamjaap.presentation.counter.WidgetRefresher
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CounterViewModelTest {

    // Ye line Step 1 wale Rule ko activate karti hai — har test se pehle/baad
    // automatically chalta hai, bina humein manually kuch karne ki zaroorat.
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var mantraProfileRepository: MantraProfileRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository
    private lateinit var incrementCountUseCase: IncrementCountUseCase
    private lateinit var getTodayStatsUseCase: GetTodayStatsUseCase
    private lateinit var viewModel: CounterViewModel

    private val fakeProfile = MantraProfile(
        id = 1L,
        name = "Om",
        malaTarget = 108,
        lifetimeCount = 50,
        isDefault = true
    )

    @Before
    fun setUp() {
        val widgetRefresher = mockk<WidgetRefresher>(relaxed = true)   // ← Context ki jagah ye

        mantraProfileRepository = mockk(relaxed = true)
        userPreferencesRepository = mockk(relaxed = true)
        incrementCountUseCase = mockk(relaxed = true)
        getTodayStatsUseCase = mockk(relaxed = true)

        // ARRANGE — jab profiles list maangi jaye, hamara ek fake profile do
        coEvery { mantraProfileRepository.getAllProfiles() } returns flowOf(listOf(fakeProfile))

        // ARRANGE — DataStore preference mein koi last-selected profile nahi hai
        coEvery { userPreferencesRepository.userPreferencesFlow } returns flowOf(
            UserPreferences(lastSelectedMantraProfileId = null)
        )

        // ARRANGE — aaj ka koi stat nahi hai abhi
        coEvery { getTodayStatsUseCase(any()) } returns null

        viewModel = CounterViewModel(
            widgetRefresher,
            mantraProfileRepository,
            userPreferencesRepository,
            incrementCountUseCase,
            getTodayStatsUseCase
        )
    }

    @Test
    fun `initial state loads the default profile`() = runTest {
        // ViewModel ke andar coroutines chal rahi hain (init block se) —
        // isse turant complete karne ke liye "advance" karna padता hai.
        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals("Om", state.currentProfile?.name)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `tapping increments the mala count optimistically`() = runTest {
        advanceUntilIdle()

        // ARRANGE — jab increment use-case call ho, ye result do
        coEvery { incrementCountUseCase(any(), any(), any()) } returns IncrementResult(
            newCount = 1, malaJustCompleted = false, totalMalasToday = 0
        )

        // ACT
        viewModel.onTap()
        advanceUntilIdle()

        // ASSERT
        val state = viewModel.uiState.value
        assertEquals(1, state.currentMalaCount)
    }

    @Test
    fun `reaching mala target shows completion animation`() = runTest {
        // ARRANGE — is baar getTodayStatsUseCase se batao ki AAJ PEHLE SE 107 count hai
        // (taaki agla tap genuinely 108 tak pahunche — jo ViewModel khud calculate karta hai)
        coEvery { getTodayStatsUseCase(any()) } returns com.am.naamjaap.domain.model.DailyStat(
            dateEpochDay = 0L,
            mantraProfileId = 1L,
            totalCount = 107,
            malaCompletions = 0
        )

        // ViewModel dobara banana padega taaki naya mock-setup use ho
        // (kyunki purana viewModel setUp() ke waqt hi initialize ho chuka tha)
        viewModel = CounterViewModel(
            mockk(relaxed = true),
            mantraProfileRepository,
            userPreferencesRepository,
            incrementCountUseCase,
            getTodayStatsUseCase
        )
        advanceUntilIdle()

        coEvery { incrementCountUseCase(any(), any(), any()) } returns IncrementResult(
            newCount = 0, malaJustCompleted = true, totalMalasToday = 1
        )

        // ACT — ab tap karne se 107 → 108 genuinely hoga
        viewModel.onTap()
        advanceUntilIdle()

        // ASSERT
        val state = viewModel.uiState.value
        assertEquals(true, state.showMalaCompleteAnimation)
    }
}