package com.am.naamjaap.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.am.naamjaap.domain.model.DailyStat
import com.am.naamjaap.presentation.components.AppTopBar
import com.am.naamjaap.presentation.theme.SaffronGlow
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            AppTopBar(title = "Your Progress", onBack = onBack)
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 12.dp)
                .padding(20.dp)
        ) {
            val accentColor = runCatching { Color(uiState.accentColorHex.toColorInt()) }
                .getOrDefault(Color(0xFFE8A33D))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(accentColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = uiState.mantraName,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    StatCard(label = "Current Streak", value = "${uiState.currentStreak} days")
                }
                Box(modifier = Modifier.weight(1f)) {
                    StatCard(label = "Lifetime", value = formatCount(uiState.lifetimeCount))
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(text = "Last 7 Days", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            WeeklyBarChart(weeklyStats = uiState.weeklyStats)

            Spacer(modifier = Modifier.height(28.dp))

            Text(text = "Milestones", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(12.dp))
            MilestoneRow(
                achieved = uiState.achievedMilestones.map { it.label }.toSet(),
                nextLabel = uiState.nextMilestone?.label
            )
        }
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun WeeklyBarChart(weeklyStats: List<DailyStat>) {
    val maxCount = (weeklyStats.maxOfOrNull { it.totalCount } ?: 0).coerceAtLeast(1)
    val maxBarHeight = 100.dp
    val labelColor = MaterialTheme.colorScheme.onBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        weeklyStats.forEach { stat ->
            val date = LocalDate.ofEpochDay(stat.dateEpochDay)
            val heightFraction = (stat.totalCount / maxCount.toFloat()).coerceIn(0f, 1f)
            val barHeight = (maxBarHeight * heightFraction).coerceAtLeast(4.dp)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = if (stat.totalCount > 0) stat.totalCount.toString() else "",
                    style = MaterialTheme.typography.labelSmall,
                    color = labelColor

                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(barHeight)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (stat.totalCount > 0) SaffronGlow
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)
                        )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(1),
                    style = MaterialTheme.typography.labelSmall,
                    color = labelColor.copy(alpha = 0.6f)
                )
            }
        }
    }
}
@Composable
private fun MilestoneRow(achieved: Set<String>, nextLabel: String?) {
    val allLabels = HistoryUiState.ALL_MILESTONES.map { it.label }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(allLabels) { label ->
            val isAchieved = label in achieved
            val isNext = label == nextLabel

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (isAchieved) SaffronGlow.copy(alpha = 0.25f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Icon(
                    imageVector = if (isAchieved) Icons.Filled.WorkspacePremium else Icons.Outlined.Lock,
                    contentDescription = if (isAchieved) "Achieved" else "Locked",
                    tint = if (isAchieved) SaffronGlow else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(28.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isAchieved) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                if (isNext) {
                    Text(
                        text = "Next",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

private fun formatCount(count: Long): String = when {
    count >= 10_000_000 -> "%.1fCr".format(count / 10_000_000.0)
    count >= 100_000 -> "%.1fL".format(count / 100_000.0)
    count >= 1_000 -> "%.1fK".format(count / 1_000.0)
    else -> count.toString()
}