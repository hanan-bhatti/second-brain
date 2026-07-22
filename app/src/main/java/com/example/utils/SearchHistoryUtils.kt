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

import android.content.SharedPreferences

fun getSearchHistory(prefs: SharedPreferences): List<String> {
    val historyStr = prefs.getString("search_history", "") ?: ""
    return if (historyStr.isBlank()) emptyList() else historyStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}

fun saveSearchHistory(prefs: SharedPreferences, history: List<String>) {
    prefs.edit().putString("search_history", history.joinToString(",")).apply()
}

fun addQueryToHistory(prefs: SharedPreferences, query: String) {
    if (query.isBlank()) return
    val trimmed = query.trim()
    val current = getSearchHistory(prefs).toMutableList()
    current.remove(trimmed)
    current.add(0, trimmed)
    val limited = current.take(10) // keep last 10
    saveSearchHistory(prefs, limited)
}

fun removeQueryFromHistory(prefs: SharedPreferences, query: String) {
    val current = getSearchHistory(prefs).toMutableList()
    current.remove(query.trim())
    saveSearchHistory(prefs, current)
}

fun clearSearchHistory(prefs: SharedPreferences) {
    prefs.edit().remove("search_history").apply()
}
