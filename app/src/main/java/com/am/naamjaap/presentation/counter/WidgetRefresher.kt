package com.am.naamjaap.presentation.counter

/**
 * Abstracts widget refresh so CounterViewModel doesn't call Android/Glance
 * framework classes directly — keeps it unit-testable without a real device.
 */
interface WidgetRefresher {
    suspend fun refresh()
}