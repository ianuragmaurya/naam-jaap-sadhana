package com.am.naamjaap.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.am.naamjaap.di.widgetEntryPoint

private val WidgetBackground = Color(0xFF1A0F2E)
private val WidgetSurface = Color(0xFF2A1F3D)
private val WidgetAccent = Color(0xFFE8A33D)

class JapaWidget : GlanceAppWidget() {

    companion object {
        val KEY_PROFILE_ID = longPreferencesKey("profileId")
        val KEY_MANTRA_NAME = stringPreferencesKey("mantraName")
        val KEY_MALA_TARGET = intPreferencesKey("malaTarget")
        val KEY_CURRENT_COUNT = intPreferencesKey("currentCount")
    }

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Load/cache data BEFORE entering provideContent — provideContent's
        // lambda is a @Composable block, not a coroutine, so no suspend
        // calls (like updateAppWidgetState) can safely run inside it.
        val prefsBefore = getAppWidgetState(context, PreferencesGlanceStateDefinition, id)
        val cachedProfileId = prefsBefore[KEY_PROFILE_ID]

        if (cachedProfileId == null) {
            // First render only (widget just added) — load real data once
            // from Room/DataStore, then cache it in Glance's own fast state
            // so every future render (including each tap) skips this.
            val fresh = JapaWidgetDataLoader.load(context)
            if (fresh != null) {
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { p ->
                    p.toMutablePreferences().apply {
                        this[KEY_PROFILE_ID] = fresh.profileId
                        this[KEY_MANTRA_NAME] = fresh.mantraName
                        this[KEY_MALA_TARGET] = fresh.malaTarget
                        this[KEY_CURRENT_COUNT] = fresh.currentMalaCount
                    }
                }
            }
        }

        provideContent {
            val prefs = currentState<Preferences>()
            val profileId = prefs[KEY_PROFILE_ID]

            val data = if (profileId != null) {
                JapaWidgetData(
                    mantraName = prefs[KEY_MANTRA_NAME] ?: "",
                    profileId = profileId,
                    malaTarget = prefs[KEY_MALA_TARGET] ?: 108,
                    currentMalaCount = prefs[KEY_CURRENT_COUNT] ?: 0
                )
            } else {
                null
            }

            WidgetContent(data)
        }
    }

    @Composable
    private fun WidgetContent(data: JapaWidgetData?) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(4.dp)
                .background(ColorProvider(day = WidgetBackground, night = WidgetBackground))
                .cornerRadius(28.dp)
                .clickable(actionRunCallback<IncrementWidgetAction>())
        ) {
            if (data == null) {
                EmptyState()
            } else {
                FilledState(data)
            }
        }
    }

    @Composable
    private fun EmptyState() {
        Column(
            modifier = GlanceModifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = "🕉️",
                style = TextStyle(fontSize = 28.sp, textAlign = TextAlign.Center)
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = "Open Naam Jaap",
                style = TextStyle(
                    color = ColorProvider(day = Color.White, night = Color.White),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
    }

    @Composable
    private fun FilledState(data: JapaWidgetData) {
        Column(
            modifier = GlanceModifier.fillMaxSize().padding(14.dp),
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = data.mantraName,
                style = TextStyle(
                    color = ColorProvider(day = WidgetAccent, night = WidgetAccent),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                ),
                maxLines = 1
            )

            Spacer(modifier = GlanceModifier.height(6.dp))

            Text(
                text = data.currentMalaCount.toString(),
                style = TextStyle(
                    color = ColorProvider(day = Color.White, night = Color.White),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = GlanceModifier.height(2.dp))

            Text(
                text = "of ${data.malaTarget}",
                style = TextStyle(
                    color = ColorProvider(day = Color(0xB3FFFFFF), night = Color(0xB3FFFFFF)),
                    fontSize = 10.sp
                )
            )

            Spacer(modifier = GlanceModifier.height(10.dp))

            val progress = (data.currentMalaCount.toFloat() / data.malaTarget.toFloat()).coerceIn(0f, 1f)
            ProgressBar(progress = progress)
        }
    }

    @Composable
    private fun ProgressBar(progress: Float) {
        val filledWeight = (progress * 100).toInt().coerceIn(1, 99)
        val unfilledWeight = 100 - filledWeight

        Row(
            modifier = GlanceModifier
                .width(140.dp)
                .height(6.dp)
                .cornerRadius(3.dp)
                .background(ColorProvider(day = WidgetSurface, night = WidgetSurface))
        ) {
            Box(
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxSize()
                    .background(ColorProvider(day = WidgetAccent, night = WidgetAccent))
            ) {}
            if (unfilledWeight > 0) {
                Spacer(modifier = GlanceModifier.defaultWeight())
            }
        }
    }
}

class IncrementWidgetAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        runCatching {
            val currentPrefs = getAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId)
            val profileId = currentPrefs[JapaWidget.KEY_PROFILE_ID] ?: return@runCatching
            val malaTarget = currentPrefs[JapaWidget.KEY_MALA_TARGET] ?: 108
            val preTapCount = currentPrefs[JapaWidget.KEY_CURRENT_COUNT] ?: 0
            val newCount = (preTapCount + 1) % malaTarget

            // Update cached state + redraw FIRST — this is what makes the
            // tap feel instant, since it skips the Room/DataStore round trip.
            updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
                prefs.toMutablePreferences().apply {
                    this[JapaWidget.KEY_CURRENT_COUNT] = newCount
                }
            }
            JapaWidget().update(context, glanceId)

            // Persist the real increment to the database after — the widget
            // has already redrawn by this point.
            val entryPoint = context.widgetEntryPoint()
            entryPoint.incrementCountUseCase()(
                profileId = profileId,
                currentCount = preTapCount,
                malaTarget = malaTarget
            )
        }.onFailure {
            android.util.Log.e("JapaWidget", "Widget tap failed", it)
        }
    }
}

// Called only from in-app taps (via WidgetRefresher) — forces a true
// resync from the database, since the widget's own cached state doesn't
// know about counts changed inside the app.
suspend fun refreshAllWidgetsFromDatabase(context: Context) {
    val manager = GlanceAppWidgetManager(context)
    val glanceIds = manager.getGlanceIds(JapaWidget::class.java)
    val fresh = JapaWidgetDataLoader.load(context) ?: return
    glanceIds.forEach { id ->
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, id) { p ->
            p.toMutablePreferences().apply {
                this[JapaWidget.KEY_PROFILE_ID] = fresh.profileId
                this[JapaWidget.KEY_MANTRA_NAME] = fresh.mantraName
                this[JapaWidget.KEY_MALA_TARGET] = fresh.malaTarget
                this[JapaWidget.KEY_CURRENT_COUNT] = fresh.currentMalaCount
            }
        }
    }
    JapaWidget().updateAll(context)
}

class JapaWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = JapaWidget()
}