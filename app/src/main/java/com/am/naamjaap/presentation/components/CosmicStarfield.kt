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

private data class Star(
    val x: Float,           // 0f..1f fraction of width
    val y: Float,           // 0f..1f fraction of height
    val radius: Float,      // px
    val twinkleSpeed: Float,
    val phase: Float,
    val isGolden: Boolean,  // rare accent stars, tie back to brand
    val baseAlpha: Float
)

private data class Nebula(
    val x: Float, val y: Float, val radius: Float, val color: Color, val alpha: Float
)

/**
 * A quiet star-field for Focus Mode — evokes the "cosmic sound" idea behind
 * Om (the vibration of the universe) rather than a literal sci-fi scene.
 * Mostly-static stars with a gentle twinkle (alpha oscillation only — no
 * position animation for most stars, so this stays extremely cheap to draw
 * every frame), plus two fixed, very-low-alpha nebula blooms for depth.
 */
@Composable
fun CosmicStarfield(
    modifier: Modifier = Modifier,
    starCount: Int = 70
) {
    val goldAccent = Color(0xFFE8A33D)
    val nebulaViolet = Color(0xFF4A2E6B)
    val nebulaRose = Color(0xFF6B2E4A)

    val stars = remember {
        List(starCount) {
            Star(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                radius = Random.nextFloat() * 1.6f + 0.6f,
                twinkleSpeed = Random.nextFloat() * 0.8f + 0.4f,
                phase = Random.nextFloat() * 6.28f,
                isGolden = Random.nextFloat() < 0.08f,   // ~8% are warm gold accents
                baseAlpha = Random.nextFloat() * 0.5f + 0.35f
            )
        }
    }

    val nebulas = remember {
        listOf(
            Nebula(x = 0.15f, y = 0.20f, radius = 260f, color = nebulaViolet, alpha = 0.10f),
            Nebula(x = 0.85f, y = 0.75f, radius = 220f, color = nebulaRose, alpha = 0.08f)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "cosmic")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.283f,   // one full sine cycle
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "twinkleTime"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        nebulas.forEach { drawNebula(it) }
        stars.forEach { drawStar(it, time, goldAccent) }
    }
}

private fun DrawScope.drawNebula(nebula: Nebula) {
    drawCircle(
        color = nebula.color.copy(alpha = nebula.alpha),
        radius = nebula.radius,
        center = Offset(nebula.x * size.width, nebula.y * size.height)
    )
}

private fun DrawScope.drawStar(star: Star, time: Float, goldAccent: Color) {
    // Pure alpha oscillation — no position change, so this is essentially
    // free to redraw every frame even with ~70 stars on screen.
    val twinkle = (sin(time * star.twinkleSpeed + star.phase) + 1f) / 2f  // 0..1
    val alpha = star.baseAlpha * (0.5f + 0.5f * twinkle)

    val color = if (star.isGolden) goldAccent else Color.White
    drawCircle(
        color = color.copy(alpha = alpha),
        radius = star.radius,
        center = Offset(star.x * size.width, star.y * size.height)
    )
}