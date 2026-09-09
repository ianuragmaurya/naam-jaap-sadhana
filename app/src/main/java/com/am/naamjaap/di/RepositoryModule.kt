package com.am.naamjaap.di

import com.am.naamjaap.data.repository.DailyStatRepositoryImpl
import com.am.naamjaap.data.repository.JapaSessionRepositoryImpl
import com.am.naamjaap.data.repository.MantraProfileRepositoryImpl
import com.am.naamjaap.data.repository.UserPreferencesRepositoryImpl
import com.am.naamjaap.domain.repository.DailyStatRepository
import com.am.naamjaap.domain.repository.JapaSessionRepository
import com.am.naamjaap.domain.repository.MantraProfileRepository
import com.am.naamjaap.domain.repository.UserPreferencesRepository
import com.am.naamjaap.presentation.counter.GlanceWidgetRefresher
import com.am.naamjaap.presentation.counter.WidgetRefresher
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMantraProfileRepository(
        impl: MantraProfileRepositoryImpl
    ): MantraProfileRepository

    @Binds
    @Singleton
    abstract fun bindJapaSessionRepository(
        impl: JapaSessionRepositoryImpl
    ): JapaSessionRepository

    @Binds
    @Singleton
    abstract fun bindDailyStatRepository(
        impl: DailyStatRepositoryImpl
    ): DailyStatRepository

    @Binds
    @Singleton
    abstract fun bindUserPreferencesRepository(
        impl: UserPreferencesRepositoryImpl
    ): UserPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindWidgetRefresher(
        impl: GlanceWidgetRefresher
    ): WidgetRefresher
}