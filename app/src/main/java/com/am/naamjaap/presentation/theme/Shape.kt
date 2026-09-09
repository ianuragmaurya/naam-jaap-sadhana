package com.am.naamjaap.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val NaamJaapShapes = Shapes(
    // Chips, small buttons, badges
    extraSmall = RoundedCornerShape(8.dp),

    // Standard buttons, text fields
    small = RoundedCornerShape(12.dp),

    // Cards — stat cards, mantra profile cards, glass panels
    medium = RoundedCornerShape(20.dp),

    // Bottom sheets, dialogs, mantra selector sheet
    large = RoundedCornerShape(28.dp),

    // Big feature surfaces — onboarding cards, milestone celebration panel
    extraLarge = RoundedCornerShape(36.dp)
)

// ---------- Custom expressive shapes (beyond default M3 scale) ----------

// The main tap button on Counter screen — perfectly circular, mimics a mala bead
val MalaBeadButtonShape = RoundedCornerShape(percent = 50)

// Slightly squircle-ish shape for stat cards (softer than medium, more "blobby")
val SquircleShape = RoundedCornerShape(
    topStart = 24.dp,
    topEnd = 32.dp,
    bottomStart = 32.dp,
    bottomEnd = 24.dp
)

// Bottom sheet top-only rounding (mantra selector, settings sheet)
val SheetTopShape = RoundedCornerShape(
    topStart = 28.dp,
    topEnd = 28.dp
)