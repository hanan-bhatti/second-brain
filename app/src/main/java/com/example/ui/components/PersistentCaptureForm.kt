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

import android.content.Context
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.SavedItemType
import com.example.ui.viewmodel.SecondBrainViewModel

@Composable
fun PersistentCaptureForm(viewModel: SecondBrainViewModel) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("draft_prefs", Context.MODE_PRIVATE) }
    var noteText by remember { mutableStateOf(prefs.getString("quick_note", "") ?: "") }
    var noteTags by remember { mutableStateOf(prefs.getString("quick_tags", "") ?: "") }
    val focusManager = LocalFocusManager.current
    var isExpanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(noteText) {
        prefs.edit().putString("quick_note", noteText).apply()
    }
    LaunchedEffect(noteTags) {
        prefs.edit().putString("quick_tags", noteTags).apply()
    }

    val commonTags = listOf("Idea", "Todo", "Important", "Work", "Personal")

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = if (isExpanded) 16.dp else 0.dp,
        shape = if (isExpanded) RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp) else RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = if (isExpanded) 0.dp else 16.dp, vertical = if (isExpanded) 0.dp else 12.dp)
            .animateContentSize(animationSpec = tween(durationMillis = 250, easing = LinearOutSlowInEasing))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = if (isExpanded) 16.dp else 4.dp)
        ) {
            if (!isExpanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { isExpanded = true }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (noteText.isEmpty()) "Quick note..." else noteText,
                        color = if (noteText.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    )
                    IconButton(
                        onClick = {
                            if (noteText.isNotBlank()) {
                                val tagsList = noteTags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                viewModel.saveLocalTextItem(
                                    title = "Quick Note",
                                    content = noteText,
                                    type = SavedItemType.TEXT,
                                    selectedFolders = tagsList
                                )
                                noteText = ""
                                noteTags = ""
                            } else {
                                isExpanded = true
                            }
                        }
                    ) {
                        Icon(painter = painterResource(id = R.drawable.ic_custom_send), contentDescription = "Save Note", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Quick Capture", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = {
                        isExpanded = false
                        focusManager.clearFocus()
                    }) {
                        Icon(painter = painterResource(id = R.drawable.ic_custom_chevron_down), contentDescription = "Collapse")
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = { Text("Quick note...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp, max = 160.dp)
                        .focusRequester(focusRequester),
                    shape = RoundedCornerShape(20.dp),
                    maxLines = 6,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = noteTags,
                    onValueChange = { noteTags = it },
                    placeholder = { Text("Tags (comma separated)...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(commonTags) { tag ->
                        FilterChip(
                            selected = false,
                            onClick = {
                                val currentTags = noteTags.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableList()
                                if (!currentTags.contains(tag)) {
                                    currentTags.add(tag)
                                    noteTags = currentTags.joinToString(", ")
                                }
                            },
                            label = { Text(tag) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (noteText.isNotBlank()) {
                            val tagsList = noteTags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            viewModel.saveLocalTextItem(
                                title = "Quick Note",
                                content = noteText,
                                type = SavedItemType.TEXT,
                                selectedFolders = tagsList
                            )
                            noteText = ""
                            noteTags = ""
                            isExpanded = false
                            focusManager.clearFocus()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(painter = painterResource(id = R.drawable.ic_custom_send), contentDescription = "Save Note")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Note")
                }
            }
        }
    }
}
