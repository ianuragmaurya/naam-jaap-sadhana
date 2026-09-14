package com.am.naamjaap.presentation.focusmode

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.am.naamjaap.presentation.counter.CounterViewModel
import com.am.naamjaap.presentation.counter.performTapFeedback
import com.am.naamjaap.presentation.theme.SaffronGlow
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun FocusModeScreen(
    onExit: () -> Unit,
    viewModel: CounterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val view = LocalView.current

    DisposableEffect(Unit) {
        val activity = view.context.findActivity()
        val controller = activity?.let { WindowCompat.getInsetsController(it.window, view) }
        controller?.hide(WindowInsetsCompat.Type.systemBars())
        controller?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        onDispose { controller?.show(WindowInsetsCompat.Type.systemBars()) }
    }

    DisposableEffect(uiState.volumeButtonCountingEnabled) {
        if (uiState.volumeButtonCountingEnabled) {
            VolumeKeyHandler.register {
                performTapFeedback(context, uiState.hapticIntensity)
                viewModel.onTap()
            }
        }
        onDispose { VolumeKeyHandler.unregister() }
    }

    LaunchedEffect(uiState.showMalaCompleteAnimation) {
        if (uiState.showMalaCompleteAnimation) {
            performTapFeedback(context, uiState.hapticIntensity)
            delay(1400.milliseconds)
            viewModel.onMalaCompleteAnimationShown()
        }
    }

    BackHandler(onBack = onExit)

    // Slow, subtle "breathing" glow behind the count — gives the screen life
    // without being distracting or battery-heavy (just an alpha interpolation).
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breathAlpha by infiniteTransition.animateFloat(
        initialValue = 0.06f,
        targetValue = 0.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathAlpha"
    )

    // Quick pulse on every tap — the screen itself "responds" to your chanting.
    var pulseTrigger by remember { mutableFloatStateOf(0f) }
    val tapScale by animateFloatAsState(
        targetValue = pulseTrigger,
        animationSpec = spring(dampingRatio = 0.35f, stiffness = 200f),
        label = "tapPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    performTapFeedback(context, uiState.hapticIntensity)
                    pulseTrigger = if (pulseTrigger == 0f) 1f else 0f
                    viewModel.onTap()
                }
            )
    ) {
        // Breathing radial glow, centered
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SaffronGlow.copy(alpha = breathAlpha),
                            Color.Transparent
                        ),
                        radius = 700f
                    )
                )
        )

        // Mala-complete — full-screen soft golden wash, dignified not gamey
        val completeAlpha by animateFloatAsState(
            targetValue = if (uiState.showMalaCompleteAnimation) 0.18f else 0f,
            animationSpec = tween(600),
            label = "completeWash"
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SaffronGlow.copy(alpha = completeAlpha))
        )

        // Mantra name — top, understated
        Text(
            text = uiState.currentProfile?.name ?: "",
            color = Color(0xFFE8A33D).copy(alpha = 0.7f),
            style = MaterialTheme.typography.titleMedium.copy(letterSpacing = 2.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 64.dp)
        )

        // The count — now the clear visual center of the screen
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(
                targetState = uiState.currentMalaCount,
                transitionSpec = {
                    (fadeIn(tween(200)) togetherWith fadeOut(tween(150)))
                },
                label = "countChange"
            ) { count ->
                Text(
                    text = count.toString(),
                    color = Color.White.copy(alpha = 0.92f),
                    fontSize = 96.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.scale(1f + (tapScale * 0.04f))
                )
            }

            Text(
                text = "mala ${uiState.totalMalasToday + 1} · target ${uiState.currentProfile?.malaTarget ?: 108}",
                color = Color.White.copy(alpha = 0.3f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        // Gentle hint for first-time users — fades away, doesn't clutter permanently
        var showHint by remember { mutableFloatStateOf(1f) }
        LaunchedEffect(Unit) {
            delay(2500.milliseconds)
            showHint = 0f
        }
        val hintAlpha by animateFloatAsState(
            targetValue = showHint * 0.4f,
            animationSpec = tween(800),
            label = "hintFade"
        )
        Text(
            text = "Tap anywhere or use volume buttons",
            color = Color.White.copy(alpha = hintAlpha),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
        )

        IconButton(
            onClick = onExit,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(Color.White.copy(alpha = 0.08f), CircleShape)

        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Exit focus mode",
                tint = Color.White.copy(alpha = 0.55f)
            )
        }
    }
}