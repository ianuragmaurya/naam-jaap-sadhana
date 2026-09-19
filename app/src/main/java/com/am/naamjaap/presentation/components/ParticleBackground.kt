package com.am.naamjaap.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.sin
import kotlin.random.Random

private data class GoldMote(
    val startX: Float,      // 0f..1f, fraction of width
    val baseY: Float,       // 0f..1f, fraction of height
    val radius: Float,      // px
    val driftAmplitude: Float,
    val speedFactor: Float, // varies per mote so they don't move in lockstep
    val phaseOffset: Float,
    val baseAlpha: Float
)

/**
 * Very light, battery-friendly ambient background: a handful of soft golden
 * "dust motes" drifting slowly upward with gentle side-to-side sway, evoking
 * incense smoke / diya glow rather than a literal particle-system. Only
 * alpha/position interpolation — no per-frame allocation, no physics.
 */
@Composable
fun GoldDustBackground(
    modifier: Modifier = Modifier,
    moteCount: Int = 10,
    color: Color = Color(0xFFE8A33D)
) {
    val motes = remember {
        List(moteCount) {
            GoldMote(
                startX = Random.nextFloat(),
                baseY = Random.nextFloat(),
                radius = Random.nextFloat() * 2.5f + 1.5f,
                driftAmplitude = Random.nextFloat() * 18f + 8f,
                speedFactor = Random.nextFloat() * 0.6f + 0.7f,
                phaseOffset = Random.nextFloat() * 6.28f,
                baseAlpha = Random.nextFloat() * 0.25f + 0.10f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "goldDust")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        motes.forEach { mote ->
            drawMote(mote, time, color)
        }
    }
}

private fun DrawScope.drawMote(mote: GoldMote, time: Float, color: Color) {
    // Upward drift over the animation loop, wrapping back to bottom.
    val progress = (time * mote.speedFactor + mote.phaseOffset / 6.28f) % 1f
    val y = (mote.baseY - progress) .let { if (it < 0f) it + 1f else it } * size.height

    // Gentle horizontal sway using a sine wave — cheap, no per-frame state.
    val sway = sin((time * 6.28f * mote.speedFactor) + mote.phaseOffset) * mote.driftAmplitude
    val x = mote.startX * size.width + sway

    // Fade in/out near the top and bottom edges so motes don't "pop".
    val edgeFade = when {
        progress < 0.08f -> progress / 0.08f
        progress > 0.85f -> (1f - progress) / 0.15f
        else -> 1f
    }.coerceIn(0f, 1f)

    drawCircle(
        color = color.copy(alpha = mote.baseAlpha * edgeFade),
        radius = mote.radius,
        center = Offset(x, y)
    )
}