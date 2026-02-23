package ru.mycottege.app.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val STORE_NAME = "settings_prefs"
private val Context.dataStore by preferencesDataStore(name = STORE_NAME)

private object Keys {
  val themeMode = intPreferencesKey("theme_mode") // 0=SYSTEM, 1=LIGHT, 2=DARK
  val dynamicColor = booleanPreferencesKey("dynamic_color")
}

class SettingsPrefs(private val context: Context) {

  val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
    when (prefs[Keys.themeMode] ?: 0) {
      1 -> ThemeMode.LIGHT
      2 -> ThemeMode.DARK
      else -> ThemeMode.SYSTEM
    }
  }

  val dynamicColor: Flow<Boolean> = context.dataStore.data.map { prefs ->
    prefs[Keys.dynamicColor] ?: true
  }

  suspend fun setThemeMode(mode: ThemeMode) {
    context.dataStore.edit { prefs ->
      prefs[Keys.themeMode] = when (mode) {
        ThemeMode.SYSTEM -> 0
        ThemeMode.LIGHT -> 1
        ThemeMode.DARK -> 2
      }
    }
  }

  suspend fun setDynamicColor(enabled: Boolean) {
    context.dataStore.edit { prefs ->
      prefs[Keys.dynamicColor] = enabled
    }
  }
}
