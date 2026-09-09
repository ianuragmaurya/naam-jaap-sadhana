package com.am.naamjaap.presentation.settings

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.am.naamjaap.data.local.datastore.HapticIntensity
import com.am.naamjaap.data.local.datastore.ThemeMode
import androidx.core.net.toUri
import com.am.naamjaap.presentation.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Launchers and effects belong at the top level of the composable —
    // not buried inside the scrolling Column — so they're set up once,
    // regardless of scroll position or recomposition of content below.
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri -> uri?.let { viewModel.exportBackup(it) } }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { viewModel.importBackup(it) } }

    LaunchedEffect(uiState.backupMessage) {
        uiState.backupMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onBackupMessageShown()
        }
    }

    val versionName = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "1.0"
    }

    fun openUrl(url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
    }

    Scaffold(
        topBar = {
            AppTopBar(title = "Settings", onBack = onBack)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(top = 8.dp)
                .padding(bottom = 24.dp)
        ) {
            // ---------- Appearance ----------
            SectionLabel("Appearance")
            SettingsGroup {
                ThemeMode.entries.forEachIndexed { index, mode ->
                    RadioRow(
                        label = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                        selected = uiState.themeMode == mode,
                        onClick = { viewModel.onThemeModeSelected(mode) }
                    )
                    if (index < ThemeMode.entries.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        )
                    }
                }
            }

            // ---------- Focus Mode ----------
            SectionLabel("Focus Mode")
            SettingsGroup {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text("Volume buttons to count") },
                    supportingContent = { Text("Use volume up/down to count while in Focus Mode") },
                    trailingContent = {
                        Switch(
                            checked = uiState.volumeButtonCountingEnabled,
                            onCheckedChange = viewModel::onVolumeButtonCountingToggled
                        )
                    }
                )
            }

            // ---------- Haptic Feedback ----------
            SectionLabel("Haptic Feedback")
            SettingsGroup {
                HapticIntensity.entries.forEachIndexed { index, intensity ->
                    RadioRow(
                        label = intensity.name.lowercase().replaceFirstChar { it.uppercase() },
                        selected = uiState.hapticIntensity == intensity,
                        onClick = { viewModel.onHapticIntensitySelected(intensity) }
                    )
                    if (index < HapticIntensity.entries.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        )
                    }
                }
            }

            // ---------- Sound ----------
            SectionLabel("Sound")
            SettingsGroup {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text("Chime on mala complete") },
                    trailingContent = {
                        Switch(checked = uiState.soundEnabled, onCheckedChange = viewModel::onSoundToggled)
                    }
                )
            }

            // ---------- Default Mala Size ----------
            SectionLabel("Default Mala Size")
            SettingsGroup {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(108, 54, 27, 21, 11).forEach { target ->
                        FilterChip(
                            selected = uiState.defaultMalaTarget == target,
                            onClick = { viewModel.onDefaultMalaTargetSelected(target) },
                            label = { Text(target.toString()) }
                        )
                    }
                }
            }

            // ---------- Daily Reminder ----------
            SectionLabel("Daily Reminder")
            SettingsGroup {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text("Enable reminder") },
                    trailingContent = {
                        Switch(checked = uiState.dailyReminderEnabled, onCheckedChange = viewModel::onReminderToggled)
                    }
                )
                if (uiState.dailyReminderEnabled) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        headlineContent = { Text("Reminder time") },
                        supportingContent = { Text(formatTime(uiState.dailyReminderHour, uiState.dailyReminderMinute)) },
                        trailingContent = {
                            TextButton(onClick = viewModel::onOpenTimePicker) { Text("Change") }
                        }
                    )
                }
            }

            // ---------- Backup & Restore ----------
            SectionLabel("Backup & Restore")
            SettingsGroup {
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text("Export data") },
                    supportingContent = { Text("Save your chanting history as a file") },
                    leadingContent = { Icon(Icons.Default.CloudUpload, contentDescription = null) },
                    modifier = Modifier.clickable {
                        exportLauncher.launch("naamjaap_backup_${System.currentTimeMillis()}.json")
                    }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                )
                ListItem(
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    headlineContent = { Text("Import data") },
                    supportingContent = { Text("Restore from a previously exported file") },
                    leadingContent = { Icon(Icons.Default.CloudDownload, contentDescription = null) },
                    modifier = Modifier.clickable {
                        importLauncher.launch(arrayOf("application/json"))
                    }
                )
            }

            // ---------- About & Support ----------
            SectionLabel("About & Support")
            SettingsGroup {
                val aboutItems = listOf(
                    Triple("Privacy Policy", AppLinks.PRIVACY_POLICY_URL, Icons.Outlined.PrivacyTip),
                    Triple("Terms & Conditions", AppLinks.TERMS_URL, Icons.Outlined.Description),
                    Triple("Help", AppLinks.HELP_URL, Icons.AutoMirrored.Outlined.HelpOutline),
                    Triple("Rate Us", AppLinks.PLAY_STORE_URL, Icons.Outlined.StarOutline),
                    Triple("More Apps", AppLinks.MORE_APPS_URL, Icons.Outlined.Apps)
                )
                aboutItems.forEach { (label, url, icon) ->
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        leadingContent = { Icon(icon, contentDescription = null) },
                        headlineContent = { Text(label) },
                        modifier = Modifier.clickable { openUrl(url) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                    )
                }
            }
            ListItem(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                headlineContent = { Text("Version") },
                supportingContent = { Text(versionName) }
            )
        }
    }

    if (uiState.isTimePickerOpen) {
        val timePickerState = rememberTimePickerState(
            initialHour = uiState.dailyReminderHour,
            initialMinute = uiState.dailyReminderMinute,
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = viewModel::onDismissTimePicker,
            confirmButton = {
                TextButton(onClick = {
                    viewModel.onReminderTimeConfirmed(timePickerState.hour, timePickerState.minute)
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissTimePicker) { Text("Cancel") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }
}
@Composable
private fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(content = content)
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun RadioRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

private fun formatTime(hour: Int, minute: Int): String {
    val period = if (hour < 12) "AM" else "PM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return "%02d:%02d %s".format(displayHour, minute, period)
}