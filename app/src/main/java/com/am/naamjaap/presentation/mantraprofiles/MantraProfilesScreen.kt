package com.am.naamjaap.presentation.mantraprofiles

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.am.naamjaap.presentation.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MantraProfilesScreen(
    onBack: () -> Unit,
    viewModel: MantraProfilesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onErrorShown()
        }
    }

    Scaffold(
        topBar = {
                AppTopBar(title = "Mantra Profiles", onBack = onBack)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add mantra")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
                .padding(top = 8.dp)
                , contentPadding = PaddingValues(bottom = 88.dp)

        ) {
            items(uiState.profiles, key = { it.id }) { profile ->
                val accentColor = runCatching { Color(profile.accentColorHex.toColorInt()) }
                    .getOrDefault(Color(0xFFE8A33D))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    ListItem(
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        leadingContent = {
                            Box(modifier = Modifier.size(14.dp).background(accentColor, CircleShape))
                        },
                        headlineContent = { Text(profile.name, fontWeight = FontWeight.Medium) },
                        supportingContent = {
                            Text("Target: ${profile.malaTarget} · Lifetime: ${profile.lifetimeCount}")
                        },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { viewModel.onEditClick(profile) }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit ${profile.name}")
                                }
                                IconButton(onClick = { viewModel.onDeleteRequest(profile) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete ${profile.name}",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    if (uiState.isAddDialogOpen) {
        val isEditing = uiState.editingProfile != null
        val unusedSuggestions = uiState.availableSuggestions.filterNot { suggestion ->
            uiState.profiles.any { it.name.equals(suggestion, ignoreCase = true) }
        }

        AlertDialog(
            onDismissRequest = viewModel::onDismissAddDialog,
            title = { Text(if (isEditing) "Edit Mantra" else "Add Mantra", fontWeight = FontWeight.SemiBold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = uiState.newMantraName,
                        onValueChange = viewModel::onNewNameChanged,
                        label = { Text("Mantra name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    if (!isEditing && unusedSuggestions.isNotEmpty()) {
                        Text(
                            text = "Suggestions",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                        )
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            unusedSuggestions.forEach { suggestion ->
                                FilterChip(
                                    selected = uiState.newMantraName == suggestion,
                                    onClick = { viewModel.onSuggestionClick(suggestion) },
                                    label = { Text(suggestion, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }

                    Text(
                        text = "Mala size",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(108, 54, 27, 21, 11).forEach { target ->
                            FilterChip(
                                selected = uiState.newMalaTarget == target,
                                onClick = { viewModel.onNewMalaTargetSelected(target) },
                                label = { Text(target.toString()) }
                            )
                        }
                    }

                    Text(
                        text = "Color",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MANTRA_COLOR_OPTIONS.forEach { option ->
                            val isSelected = uiState.newAccentColorHex == option.hex
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(option.color, CircleShape)
                                    .border(
                                        width = if (isSelected) 3.dp else 0.dp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.onNewColorSelected(option.hex) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::onConfirmAdd, enabled = uiState.canSaveNewMantra) {
                    Text(if (isEditing) "Save" else "Add", fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onDismissAddDialog) { Text("Cancel") }
            }
        )
    }

    uiState.deleteTarget?.let { target ->
        AlertDialog(
            onDismissRequest = viewModel::onCancelDelete,
            title = { Text("Delete mantra?", fontWeight = FontWeight.SemiBold) },
            text = { Text("This will permanently delete \"${target.name}\" and all its chanting history.") },
            confirmButton = {
                TextButton(onClick = viewModel::onConfirmDelete) {
                    Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onCancelDelete) { Text("Cancel") }

                if (uiState.newMantraName.isNotBlank() && !uiState.canSaveNewMantra) {
                    Text(
                        text = "A mantra with this name already exists",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        )
    }
}