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

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp

@Composable
fun ImageMarkingCanvas(
    bitmap: Bitmap,
    onRegionSelected: (x: Int, y: Int, width: Int, height: Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    var pathPoints by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var containerWidth by remember { mutableStateOf(0f) }
    var containerHeight by remember { mutableStateOf(0f) }

    val currentEnabled by rememberUpdatedState(enabled)
    val currentOnRegionSelected by rememberUpdatedState(onRegionSelected)
    val currentBitmap by rememberUpdatedState(bitmap)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(bitmap.width.toFloat() / bitmap.height.toFloat())
            .onGloballyPositioned { coordinates ->
                containerWidth = coordinates.size.width.toFloat()
                containerHeight = coordinates.size.height.toFloat()
            }
            .pointerInput(bitmap) {
                detectDragGestures(
                    onDragStart = { offset ->
                        if (currentEnabled) {
                            pathPoints = listOf(offset)
                        }
                    },
                    onDrag = { change, _ ->
                        if (currentEnabled) {
                            change.consume()
                            pathPoints = pathPoints + change.position
                        }
                    },
                    onDragEnd = {
                        if (currentEnabled && pathPoints.isNotEmpty() && containerWidth > 0 && containerHeight > 0) {
                            val minX = pathPoints.minOf { it.x }.coerceIn(0f, containerWidth)
                            val maxX = pathPoints.maxOf { it.x }.coerceIn(0f, containerWidth)
                            val minY = pathPoints.minOf { it.y }.coerceIn(0f, containerHeight)
                            val maxY = pathPoints.maxOf { it.y }.coerceIn(0f, containerHeight)

                            // Map coordinates to original bitmap dimensions
                            val scaleX = currentBitmap.width / containerWidth
                            val scaleY = currentBitmap.height / containerHeight

                            val cropX = (minX * scaleX).toInt()
                            val cropY = (minY * scaleY).toInt()
                            val cropWidth = ((maxX - minX) * scaleX).toInt()
                            val cropHeight = ((maxY - minY) * scaleY).toInt()

                            currentOnRegionSelected(cropX, cropY, cropWidth, cropHeight)
                        }
                    }
                )
            }
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Shared visual asset",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            if (pathPoints.size > 1) {
                val path = Path().apply {
                    moveTo(pathPoints.first().x, pathPoints.first().y)
                    for (i in 1 until pathPoints.size) {
                        lineTo(pathPoints[i].x, pathPoints[i].y)
                    }
                }
                drawPath(
                    path = path,
                    color = primaryColor.copy(alpha = 0.6f),
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )

                val minX = pathPoints.minOf { it.x }
                val maxX = pathPoints.maxOf { it.x }
                val minY = pathPoints.minOf { it.y }
                val maxY = pathPoints.maxOf { it.y }

                drawRect(
                    color = primaryColor.copy(alpha = 0.15f),
                    topLeft = Offset(minX, minY),
                    size = Size(maxX - minX, maxY - minY),
                    style = Fill
                )
                drawRect(
                    color = primaryColor,
                    topLeft = Offset(minX, minY),
                    size = Size(maxX - minX, maxY - minY),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }
    }
}
