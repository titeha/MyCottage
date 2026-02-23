package ru.mycottege.app.legal

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "legal_prefs"
private const val CURRENT_DISCLAIMER_VERSION = 1

private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

private object Keys {
  val disclaimerAccepted = booleanPreferencesKey("disclaimer_accepted")
  val disclaimerVersion = intPreferencesKey("disclaimer_version")
}

class LegalPrefs(private val context: Context) {

  val isDisclaimerAccepted: Flow<Boolean> =
    context.dataStore.data.map { prefs ->
      val accepted = prefs[Keys.disclaimerAccepted] ?: false
      val version = prefs[Keys.disclaimerVersion] ?: 0
      accepted && version == CURRENT_DISCLAIMER_VERSION
    }

  suspend fun acceptDisclaimer() {
    context.dataStore.edit { prefs ->
      prefs[Keys.disclaimerAccepted] = true
      prefs[Keys.disclaimerVersion] = CURRENT_DISCLAIMER_VERSION
    }
  }
}
