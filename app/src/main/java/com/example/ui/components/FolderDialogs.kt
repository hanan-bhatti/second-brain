/*
 * Second Brain - A universal capture and personal knowledge archive
 * Copyright (C) 2026 Hanan Bhatti
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.CustomFolderEntity
import com.example.data.model.SavedItem
import com.example.ui.theme.DefaultFolderColor
import com.example.ui.theme.toThemeColor
import com.example.ui.viewmodel.SecondBrainViewModel

// Preset Palette of aesthetic Pastel/Vibrant Colors
val folderPresetColors = listOf(
    "#FF6B6B" to "Coral Rose",
    "#FF8E53" to "Peach Sun",
    "#FFA800" to "Soft Gold",
    "#4CAF50" to "Emerald Sage",
    "#00BFA5" to "Minty Teal",
    "#29B6F6" to "Ice Blue",
    "#5C6BC0" to "Royal Indigo",
    "#BA68C8" to "Lavender Dream",
    "#D81B60" to "Crimson Berry",
    "#78909C" to "Slate Silver",
    "#F06292" to "Flamingo Pink",
    "#FF8A65" to "Terra Cotta",
    "#FFD54F" to "Golden Silk",
    "#AED581" to "Matcha Green",
    "#4DB6AC" to "Ocean Mist",
    "#4DD0E1" to "Crystal Cyan",
    "#7986CB" to "Twilight Blue",
    "#9FA8DA" to "Periwinkle Sky",
    "#8D6E63" to "Cocoa Bean",
    "#BCAAA4" to "Desert Sand"
)

// Preset Palette of standard premium Icons
val folderPresetIcons = listOf(
    "folder",
    "star",
    "book",
    "code",
    "heart",
    "work",
    "school",
    "home",
    "shopping",
    "music",
    "tools"
)

fun parseHexColor(hex: String?, defaultColor: Color = DefaultFolderColor, isDark: Boolean = false): Color {
    if (hex.isNullOrBlank()) return defaultColor.toThemeColor(isDark)
    return try {
        Color(android.graphics.Color.parseColor(hex)).toThemeColor(isDark)
    } catch (e: Exception) {
        defaultColor.toThemeColor(isDark)
    }
}

@Composable
fun FolderIcon(
    iconName: String?,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    val resId = when (iconName) {
        "folder" -> R.drawable.ic_custom_folder
        "star" -> R.drawable.ic_custom_star
        "book" -> R.drawable.ic_custom_text
        "code" -> R.drawable.ic_custom_code
        "heart" -> R.drawable.ic_custom_heart
        "work" -> R.drawable.ic_custom_work
        "school" -> R.drawable.ic_custom_school
        "home" -> R.drawable.ic_custom_home
        "shopping" -> R.drawable.ic_custom_shopping
        "music" -> R.drawable.ic_custom_music
        "tools" -> R.drawable.ic_custom_tools
        else -> R.drawable.ic_custom_folder
    }
    Icon(
        painter = painterResource(id = resId),
        contentDescription = null,
        tint = tint,
        modifier = modifier
    )
}

@Composable
fun FolderMoveDialog(
    item: SavedItem,
    viewModel: SecondBrainViewModel,
    onDismiss: () -> Unit
) {
    val customFolders by viewModel.customFolders.collectAsState()
    var currentFolders by remember { mutableStateOf(item.folders.toSet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Move / Assign Folders") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (customFolders.isEmpty()) {
                    Text("No custom folders created yet.", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.bodyMedium)
                } else {
                    customFolders.forEach { folder ->
                        val isAssigned = currentFolders.contains(folder)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    currentFolders = if (isAssigned) {
                                        currentFolders - folder
                                    } else {
                                        currentFolders + folder
                                    }
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Checkbox(
                                checked = isAssigned,
                                onCheckedChange = { checked ->
                                    currentFolders = if (checked == true) {
                                        currentFolders + folder
                                    } else {
                                        currentFolders - folder
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(folder)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val updated = item.copy(folders = currentFolders.toList())
                    viewModel.updateSavedItem(updated)
                    onDismiss()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun FolderCustomizerDialog(
    folder: CustomFolderEntity,
    viewModel: SecondBrainViewModel,
    onDismiss: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    var folderNameInput by remember { mutableStateOf(folder.name) }
    var selectedColorHex by remember { mutableStateOf(folder.colorHex ?: folderPresetColors.first().first) }
    var selectedIconName by remember { mutableStateOf(folder.iconName ?: "folder") }
    var isPinned by remember { mutableStateOf(folder.isPinned) }

    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Folder") },
            text = { Text("Are you sure you want to delete the folder \"${folder.name}\"? This won't delete saved items in it, only the folder itself.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteFolder(folder.name)
                        showDeleteConfirm = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Customize Folder", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Rename input
                    OutlinedTextField(
                        value = folderNameInput,
                        onValueChange = { folderNameInput = it },
                        label = { Text("Folder Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("customize_folder_name_field")
                    )

                    // Pin toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { isPinned = !isPinned }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_custom_pin),
                                contentDescription = null,
                                tint = if (isPinned) parseHexColor(selectedColorHex, isDark = isDark) else MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Pin to top", fontWeight = FontWeight.Medium)
                        }
                        Switch(
                            checked = isPinned,
                            onCheckedChange = { isPinned = it },
                            thumbContent = if (isPinned) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize)
                                    )
                                }
                            } else {
                                null
                            }
                        )
                    }

                    // Choose Icon
                    Column {
                        Text(
                            text = "Choose Icon",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(folderPresetIcons.size) { index ->
                                val iconName = folderPresetIcons[index]
                                val isSelected = selectedIconName == iconName
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) parseHexColor(selectedColorHex, isDark = isDark).copy(alpha = 0.2f)
                                            else Color.Transparent
                                        )
                                        .border(
                                            1.5.dp,
                                            if (isSelected) parseHexColor(selectedColorHex, isDark = isDark)
                                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                            CircleShape
                                        )
                                        .clickable { selectedIconName = iconName },
                                    contentAlignment = Alignment.Center
                                ) {
                                    FolderIcon(
                                        iconName = iconName,
                                        tint = if (isSelected) parseHexColor(selectedColorHex, isDark = isDark) else MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Choose Color
                    Column {
                        Text(
                            text = "Choose Theme Color",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(folderPresetColors) { (hex, name) ->
                                val color = parseHexColor(hex, isDark = isDark)
                                val isSelected = selectedColorHex == hex
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .clickable { selectedColorHex = hex },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_custom_check),
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.background,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Delete button
                    Button(
                        onClick = { showDeleteConfirm = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("delete_folder_btn")
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_custom_delete),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete Folder")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val finalName = folderNameInput.trim()
                        if (finalName.isNotEmpty()) {
                            if (finalName != folder.name) {
                                viewModel.renameFolder(folder.name, finalName)
                            }
                            viewModel.updateFolder(
                                CustomFolderEntity(
                                    name = finalName,
                                    colorHex = selectedColorHex,
                                    iconName = selectedIconName,
                                    isPinned = isPinned,
                                    isSynced = folder.isSynced
                                )
                            )
                            onDismiss()
                        }
                    },
                    modifier = Modifier.testTag("save_custom_folder_btn")
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}
