package com.am.naamjaap.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.am.naamjaap.presentation.counter.CounterScreen
import com.am.naamjaap.presentation.focusmode.FocusModeScreen
import com.am.naamjaap.presentation.history.HistoryScreen
import com.am.naamjaap.presentation.mantraprofiles.MantraProfilesScreen
import com.am.naamjaap.presentation.settings.SettingsScreen

private object Routes {
    const val COUNTER = "counter"
    const val MANTRA_PROFILES = "mantra_profiles"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val FOCUS_MODE = "focus_mode"
}

@Composable
fun NaamJaapNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Routes.COUNTER) {
        composable(Routes.COUNTER) {
            CounterScreen(
                onManageMantras = { navController.navigate(Routes.MANTRA_PROFILES) },
                onViewHistory = { navController.navigate(Routes.HISTORY) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                onEnterFocusMode = { navController.navigate(Routes.FOCUS_MODE) }

            )
        }
        composable(Routes.MANTRA_PROFILES) {
            MantraProfilesScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.HISTORY) {
            HistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.FOCUS_MODE) {
            FocusModeScreen(
                onExit = { navController.popBackStack() }
            )
        }
    }
}