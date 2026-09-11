package com.am.naamjaap.presentation.counter

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.am.naamjaap.data.local.datastore.HapticIntensity

fun HapticFeedback.performTapFeedback(intensity: HapticIntensity) {
    when (intensity) {
        HapticIntensity.OFF -> { /* do nothing */ }
        HapticIntensity.LIGHT -> performHapticFeedback(HapticFeedbackType.TextHandleMove)
        HapticIntensity.MEDIUM -> performHapticFeedback(HapticFeedbackType.LongPress)
        HapticIntensity.STRONG -> performHapticFeedback(HapticFeedbackType.LongPress)
    }
}