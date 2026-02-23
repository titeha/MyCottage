package ru.mycottege.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ru.mycottege.app.ui.AppRoot
import ru.mycottege.app.ui.theme.МояДачаTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import ru.mycottege.app.settings.SettingsPrefs
import ru.mycottege.app.settings.ThemeMode

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val context = LocalContext.current
      val prefs = remember { SettingsPrefs(context) }

      val themeMode by prefs.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
      val dynamicColor by prefs.dynamicColor.collectAsState(initial = true)

      МояДачаTheme(
        themeMode = themeMode,
        dynamicColor = dynamicColor
      ) {
        AppRoot()
      }
    }
  }
}
