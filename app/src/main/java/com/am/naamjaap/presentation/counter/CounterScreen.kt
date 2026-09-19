package com.am.naamjaap.presentation.counter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.am.naamjaap.domain.model.MantraProfile
import com.am.naamjaap.presentation.theme.SaffronGlow
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.graphics.toColorInt
import com.am.naamjaap.presentation.components.GlassSurface
import com.am.naamjaap.presentation.components.GoldDustBackground

@Composable
fun CounterScreen(
    onManageMantras: () -> Unit = {},
    onViewHistory: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onEnterFocusMode: () -> Unit = {},
    viewModel: CounterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    var showMantraSheet by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.showMalaCompleteAnimation) {
        if (uiState.showMalaCompleteAnimation) {
            performTapFeedback(context, uiState.hapticIntensity)
            delay(1600.milliseconds)
            viewModel.onMalaCompleteAnimationShown()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        // Ambient gold-dust drifting behind everything — sits above the
        // gradient but below all interactive content (z-order = declaration order).
        GoldDustBackground(modifier = Modifier.fillMaxSize())

        when {
            uiState.isLoading || uiState.currentProfile == null -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            else -> {
                CounterContent(
                    uiState = uiState,
                    onTap = {
                        performTapFeedback(context, uiState.hapticIntensity)
                        viewModel.onTap()
                    },
                    onMantraNameClick = { showMantraSheet = true },
                    onViewHistory = onViewHistory,
                    onOpenSettings = onOpenSettings,
                    onEnterFocusMode = onEnterFocusMode

                )
            }
        }

        AnimatedVisibility(
            visible = uiState.showMalaCompleteAnimation,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(500)),
            modifier = Modifier.align(Alignment.Center)
        ) {
            MalaCompleteGlow()
        }
    }

    if (showMantraSheet && uiState.allProfiles.isNotEmpty()) {
        MantraSwitcherSheet(
            profiles = uiState.allProfiles,
            currentProfileId = uiState.currentProfile?.id,
            onSelect = {
                viewModel.onSwitchProfile(it)
                showMantraSheet = false
            },

            onManageMantras = {
                showMantraSheet = false
                onManageMantras()
            },
            onDismiss = { showMantraSheet = false }
        )
    }
}

@Composable
private fun CounterContent(
    uiState: CounterUiState,
    onTap: () -> Unit,
    onMantraNameClick: () -> Unit,
    onViewHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onEnterFocusMode: () -> Unit,

) {
    val profile = uiState.currentProfile ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 8.dp)
                .windowInsetsPadding(WindowInsets.statusBars),
            horizontalArrangement = Arrangement.End,
        ) {
            GlowIconButton(onClick = onEnterFocusMode, icon = Icons.Default.Fullscreen, contentDescription = "Focus mode")
            Spacer(modifier = Modifier.width(8.dp))
            GlowIconButton(onClick = onViewHistory, icon = Icons.AutoMirrored.Filled.TrendingUp, contentDescription = "View history")
            Spacer(modifier = Modifier.width(8.dp))
            GlowIconButton(onClick = onOpenSettings, icon = Icons.Default.Settings, contentDescription = "Settings")
        }
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = profile.name,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onMantraNameClick
            )
        )

        Text(
            text = "Tap name to switch mantra",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.weight(1f))

        BeadRingCounter(
            malaTarget = profile.malaTarget,
            currentCount = uiState.currentMalaCount,
            onTap = onTap
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatChip(label = "Today's malas", value = uiState.totalMalasToday.toString())
            StatChip(label = "Lifetime", value = formatCount(uiState.lifetimeCount))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun BeadRingCounter(
    malaTarget: Int,
    currentCount: Int,
    onTap: () -> Unit
) {
    val ringInteractionSource = remember { MutableInteractionSource() }
    val buttonInteractionSource = remember { MutableInteractionSource() }
    val isPressed by buttonInteractionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "tapScale"
    )
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(260.dp)
            // Makes the full 280dp area tappable, not just the inner 180dp
            // circle — taps that land outside the button still count.
            .clickable(
                interactionSource = ringInteractionSource,
                indication = null,
                onClick = onTap
            )
    ) {
        // Bead ring — 108 (or malaTarget) small dots, filled up to currentCount
        Canvas(modifier = Modifier.fillMaxSize()) {
            val ringRadius = size.minDimension / 2f - 12.dp.toPx()
            val beadRadius = (if (malaTarget <= 27) 6.dp else 4.dp).toPx()
            val center = Offset(size.width / 2f, size.height / 2f)

            for (i in 0 until malaTarget) {
                val angle = (2 * Math.PI * i / malaTarget) - (Math.PI / 2)
                val x = center.x + ringRadius * cos(angle).toFloat()
                val y = center.y + ringRadius * sin(angle).toFloat()
                val isFilled = i < currentCount

                drawCircle(
                    color = if (isFilled) SaffronGlow else onSurfaceColor.copy(alpha = 0.18f),
                    radius = beadRadius,
                    center = Offset(x, y)
                )
            }
        }

        // Central tap button — the "mala bead" itself
        Box(
            modifier = Modifier
                .size(180.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                )
                .clickable(
                    interactionSource = buttonInteractionSource,
                    indication = ripple(bounded = true, color = SaffronGlow),
                    onClick = onTap
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = currentCount.toString(),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "of $malaTarget",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.offset(y = 44.dp)
            )
        }
    }
}


@Composable
private fun StatChip(label: String, value: String) {
    GlassSurface(shape = MaterialTheme.shapes.medium) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MalaCompleteGlow() {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    // Text starts small at the center, then bounces down while growing —
    // gives it a "landing" feel rather than a flat fade-in.
    var animateIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animateIn = true }

    val scale by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "textScale"
    )
    val offsetY by animateDpAsState(
        targetValue = if (animateIn) 24.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "textOffsetY"
    )

    Box(
        modifier = Modifier
            .size(320.dp)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        SaffronGlow.copy(alpha = alpha),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🙏 Mala Complete",
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = MaterialTheme.typography.titleMedium.fontSize * scale
            ),
            color = Color.White,
            modifier = Modifier.offset(y = offsetY)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MantraSwitcherSheet(
    profiles: List<MantraProfile>,
    currentProfileId: Long?,
    onSelect: (Long) -> Unit,
    onManageMantras: () -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler(onBack = onDismiss)

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(bottom = 32.dp)) {
            Text(
                text = "Switch Mantra",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            )

            profiles.forEachIndexed { index, profile ->
                val accentColor = runCatching { Color(profile.accentColorHex.toColorInt()) }
                    .getOrDefault(Color(0xFFE8A33D))

                ListItem(
                    leadingContent = {
                        Box(modifier = Modifier.size(12.dp).background(accentColor, CircleShape))
                    },
                    headlineContent = { Text(profile.name, fontWeight = FontWeight.Medium) },
                    supportingContent = { Text("Target: ${profile.malaTarget}") },
                    trailingContent = {
                        if (profile.id == currentProfileId) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    modifier = Modifier.clickable { onSelect(profile.id) }
                )

                if (index < profiles.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            ListItem(
                headlineContent = { Text("Manage mantras") },
                leadingContent = {
                    Icon(Icons.Default.Add, contentDescription = null)
                },
                modifier = Modifier.clickable { onManageMantras() }
            )
        }
    }
}

@Composable
private fun GlowIconButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String
) {
    GlassSurface(
        shape = CircleShape,
        modifier = Modifier
            .size(44.dp)
            .clickable(onClick = onClick)
    ) {
         Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun formatCount(count: Long): String = when {
    count >= 10_000_000 -> "%.1fCr".format(count / 10_000_000.0)
    count >= 100_000 -> "%.1fL".format(count / 100_000.0)
    count >= 1_000 -> "%.1fK".format(count / 1_000.0)
    else -> count.toString()
}