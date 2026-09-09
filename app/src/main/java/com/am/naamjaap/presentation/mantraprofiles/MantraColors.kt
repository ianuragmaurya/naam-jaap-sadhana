package com.am.naamjaap.presentation.mantraprofiles

import androidx.compose.ui.graphics.Color

data class MantraColorOption(val hex: String, val color: Color)

val MANTRA_COLOR_OPTIONS = listOf(
    MantraColorOption("#E8A33D", Color(0xFFE8A33D)), // saffron
    MantraColorOption("#F48FB1", Color(0xFFF48FB1)), // rose
    MantraColorOption("#CBA6F7", Color(0xFFCBA6F7)), // violet
    MantraColorOption("#81C784", Color(0xFF81C784)), // green
    MantraColorOption("#64B5F6", Color(0xFF64B5F6)), // blue
    MantraColorOption("#E57373", Color(0xFFE57373))  // red
)