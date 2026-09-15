package com.infy.browser.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.infy.browser.model.Shortcut
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "infy_prefs")

/**
 * Persists the user's custom homepage shortcuts (on top of the built-in
 * defaults) as simple "id|label|url" strings in DataStore, so the browser
 * doesn't need a database just for a handful of shortcut tiles.
 */
class ShortcutStore(private val context: Context) {

    private val customShortcutsKey = stringSetPreferencesKey("custom_shortcuts")
    private val removedDefaultsKey = stringSetPreferencesKey("removed_default_shortcuts")

    val shortcuts: Flow<List<Shortcut>> = context.dataStore.data.map { prefs ->
        val removedDefaults = prefs[removedDefaultsKey] ?: emptySet()
        val defaults = Shortcut.defaults().filterNot { it.id in removedDefaults }

        val customRaw = prefs[customShortcutsKey] ?: emptySet()
        val custom = customRaw.mapNotNull { encoded ->
            val parts = encoded.split("|", limit = 3)
            if (parts.size == 3) Shortcut(parts[0], parts[1], parts[2]) else null
        }

        defaults + custom
    }

    suspend fun addShortcut(label: String, url: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[customShortcutsKey] ?: emptySet()
            val id = "custom_${System.currentTimeMillis()}"
            prefs[customShortcutsKey] = current + "$id|$label|$url"
        }
    }

    suspend fun removeShortcut(shortcut: Shortcut) {
        context.dataStore.edit { prefs ->
            if (shortcut.isDefault) {
                val removed = prefs[removedDefaultsKey] ?: emptySet()
                prefs[removedDefaultsKey] = removed + shortcut.id
            } else {
                val current = prefs[customShortcutsKey] ?: emptySet()
                prefs[customShortcutsKey] = current.filterNot { it.startsWith("${shortcut.id}|") }.toSet()
            }
        }
    }
}
