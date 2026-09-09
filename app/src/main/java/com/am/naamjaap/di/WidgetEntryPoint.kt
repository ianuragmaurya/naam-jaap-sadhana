package com.am.naamjaap.di

import com.am.naamjaap.domain.repository.MantraProfileRepository
import com.am.naamjaap.domain.repository.UserPreferencesRepository
import com.am.naamjaap.domain.usecase.GetTodayStatsUseCase
import com.am.naamjaap.domain.usecase.IncrementCountUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import android.content.Context

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun mantraProfileRepository(): MantraProfileRepository
    fun userPreferencesRepository(): UserPreferencesRepository
    fun incrementCountUseCase(): IncrementCountUseCase
    fun getTodayStatsUseCase(): GetTodayStatsUseCase
}

fun Context.widgetEntryPoint(): WidgetEntryPoint {
    return EntryPointAccessors.fromApplication(applicationContext, WidgetEntryPoint::class.java)
}