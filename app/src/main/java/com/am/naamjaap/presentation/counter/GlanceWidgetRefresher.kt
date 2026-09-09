package com.am.naamjaap.presentation.counter

import android.content.Context
import androidx.glance.appwidget.updateAll
import com.am.naamjaap.widget.JapaWidget
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.glance.appwidget.state.getAppWidgetState
import com.am.naamjaap.widget.refreshAllWidgetsFromDatabase

class GlanceWidgetRefresher @Inject constructor(
    @param:ApplicationContext private val context: Context
) : WidgetRefresher {
    override suspend fun refresh() {
        refreshAllWidgetsFromDatabase(context)
    }
}