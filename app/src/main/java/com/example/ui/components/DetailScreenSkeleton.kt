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

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.data.model.SavedItemType

@Composable
fun ShimmerPlaceholder(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(4.dp)
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )
    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha))
    )
}

@Composable
fun DetailScreenSkeleton(
    itemType: SavedItemType,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        // Title Skeleton
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ShimmerPlaceholder(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(28.dp),
                shape = RoundedCornerShape(8.dp)
            )
            // Meta Row Skeleton
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerPlaceholder(
                    modifier = Modifier
                        .width(100.dp)
                        .height(16.dp)
                )
                Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
                ShimmerPlaceholder(
                    modifier = Modifier
                        .width(80.dp)
                        .height(16.dp)
                )
            }
        }

        // Media / Content Specific Skeleton
        when (itemType) {
            SavedItemType.IMAGE, SavedItemType.VIDEO, SavedItemType.MEDIA -> {
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    shape = RoundedCornerShape(20.dp)
                )
            }
            SavedItemType.LINK -> {
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(20.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShimmerPlaceholder(modifier = Modifier.fillMaxWidth().height(16.dp))
                    ShimmerPlaceholder(modifier = Modifier.fillMaxWidth().height(16.dp))
                    ShimmerPlaceholder(modifier = Modifier.fillMaxWidth(0.6f).height(16.dp))
                }
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(20.dp)
                )
            }
            SavedItemType.AUDIO -> {
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    shape = RoundedCornerShape(20.dp)
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShimmerPlaceholder(modifier = Modifier.fillMaxWidth().height(16.dp))
                    ShimmerPlaceholder(modifier = Modifier.fillMaxWidth().height(16.dp))
                    ShimmerPlaceholder(modifier = Modifier.fillMaxWidth(0.7f).height(16.dp))
                }
            }
            SavedItemType.CODE -> {
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    shape = RoundedCornerShape(20.dp)
                )
            }
            SavedItemType.TEXT -> {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShimmerPlaceholder(modifier = Modifier.fillMaxWidth().height(16.dp))
                    ShimmerPlaceholder(modifier = Modifier.fillMaxWidth().height(16.dp))
                    ShimmerPlaceholder(modifier = Modifier.fillMaxWidth(0.9f).height(16.dp))
                    ShimmerPlaceholder(modifier = Modifier.fillMaxWidth().height(16.dp))
                    ShimmerPlaceholder(modifier = Modifier.fillMaxWidth(0.4f).height(16.dp))
                }
            }
        }
    }
}
