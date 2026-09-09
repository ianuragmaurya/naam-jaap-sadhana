package com.am.naamjaap.widget

import android.content.Context
import com.am.naamjaap.di.widgetEntryPoint
import kotlinx.coroutines.flow.first

data class JapaWidgetData(
    val mantraName: String,
    val profileId: Long,
    val malaTarget: Int,
    val currentMalaCount: Int
)

object JapaWidgetDataLoader {
    suspend fun load(context: Context): JapaWidgetData? {
        val entryPoint = context.widgetEntryPoint()

        val lastSelectedId = entryPoint.userPreferencesRepository()
            .userPreferencesFlow.first().lastSelectedMantraProfileId ?: return null

        val profile = entryPoint.mantraProfileRepository().getProfileById(lastSelectedId) ?: return null
        val todayStat = entryPoint.getTodayStatsUseCase()(lastSelectedId)

        return JapaWidgetData(
            mantraName = profile.name,
            profileId = profile.id,
            malaTarget = profile.malaTarget,
            currentMalaCount = (todayStat?.totalCount ?: 0) % profile.malaTarget
        )
    }
}