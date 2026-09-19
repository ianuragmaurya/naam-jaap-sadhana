package com.am.naamjaap.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A frosted-glass style surface: translucent tinted background + a subtle
 * light-catching border, evoking a temple-glass/diya-lit surface rather
 * than a flat card. Cheaper than true background-blur (which needs a
 * render-node capture) — this achieves a similar *feel* using layered
 * translucency and a gradient border, which is reliable across all
 * Android API levels this app supports (minSdk 26).
 */
@Composable
fun GlassSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    tint: Color = MaterialTheme.colorScheme.surfaceVariant,
    borderGlow: Color = MaterialTheme.colorScheme.primary,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(tint.copy(alpha = 0.38f), shape)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderGlow.copy(alpha = 0.35f),
                        borderGlow.copy(alpha = 0.05f),
                        borderGlow.copy(alpha = 0.20f)
                    )
                ),
                shape = shape
            )
    ) {
        content()
    }
}