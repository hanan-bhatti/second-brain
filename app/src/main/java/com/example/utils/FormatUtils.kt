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

package com.example.utils

import java.util.Locale

/**
 * Utility functions for formatting storage sizes, file metrics, and strings.
 */
object FormatUtils {
    fun formatStorageSize(bytes: Long): String {
        if (bytes <= 0L) return "0 B"
        val kb = bytes / 1024f
        val mb = kb / 1024f
        val gb = mb / 1024f
        return when {
            gb >= 1.0f -> String.format(Locale.US, "%.1f GB", gb)
            mb >= 1.0f -> String.format(Locale.US, "%.1f MB", mb)
            else -> String.format(Locale.US, "%.1f KB", kb)
        }
    }
}
