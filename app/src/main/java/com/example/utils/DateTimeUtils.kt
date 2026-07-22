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

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Single source of truth for date and time formatting across the app.
 */
object DateTimeUtils {
    fun formatSimpleDate(time: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(time))
    }

    fun formatLongDate(time: Long): String {
        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(time))
    }

    fun formatShortDate(time: Long): String {
        val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
        return sdf.format(Date(time))
    }

    fun formatFullDateTime(time: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(time))
    }

    fun getRelativeTimeSpanString(time: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - time
        if (diff < 0) return "Active now"
        val diffSeconds = diff / 1000
        if (diffSeconds < 60) return "Active now"
        val diffMinutes = diffSeconds / 60
        if (diffMinutes < 60) return "$diffMinutes ${if (diffMinutes == 1L) "minute" else "minutes"} ago"
        val diffHours = diffMinutes / 60
        if (diffHours < 24) return "$diffHours ${if (diffHours == 1L) "hour" else "hours"} ago"
        val diffDays = diffHours / 24
        if (diffDays < 7) return "$diffDays ${if (diffDays == 1L) "day" else "days"} ago"
        val diffWeeks = diffDays / 7
        if (diffWeeks < 4) return "$diffWeeks ${if (diffWeeks == 1L) "week" else "weeks"} ago"
        val diffMonths = diffDays / 30
        return "$diffMonths ${if (diffMonths == 1L) "month" else "months"} ago"
    }
}
