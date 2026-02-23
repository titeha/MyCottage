package ru.mycottege.app.ui.screens

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.mycottege.app.settings.SettingsPrefs
import ru.mycottege.app.settings.ThemeMode
import androidx.compose.ui.res.stringResource
import ru.mycottege.app.R
import ru.mycottege.app.settings.UnitSystem

@Composable
fun SettingsScreen() {
  val context = LocalContext.current
  val prefs = remember { SettingsPrefs(context) }
  val scope = rememberCoroutineScope()

  val themeMode by prefs.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
  val dynamicColor by prefs.dynamicColor.collectAsState(initial = true)

  val unitSystem by prefs.unitSystem.collectAsState(initial = UnitSystem.METRIC)

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    Text("Настройки", style = MaterialTheme.typography.headlineMedium)

    Text("Тема", style = MaterialTheme.typography.titleMedium)

    ThemeOption("Как в системе", themeMode == ThemeMode.SYSTEM) {
      scope.launch { prefs.setThemeMode(ThemeMode.SYSTEM) }
    }
    ThemeOption("Светлая", themeMode == ThemeMode.LIGHT) {
      scope.launch { prefs.setThemeMode(ThemeMode.LIGHT) }
    }
    ThemeOption("Тёмная", themeMode == ThemeMode.DARK) {
      scope.launch { prefs.setThemeMode(ThemeMode.DARK) }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("Динамические цвета", modifier = Modifier.weight(1f))
        Switch(
          checked = dynamicColor,
          onCheckedChange = { checked ->
            scope.launch { prefs.setDynamicColor(checked) }
          }
        )
      }
    } else {
      Text("Динамические цвета доступны на Android 12+.", style = MaterialTheme.typography.bodySmall)
    }
  }
}

@Composable
private fun ThemeOption(title: String, selected: Boolean, onClick: () -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    RadioButton(selected = selected, onClick = onClick)
    Text(title, modifier = Modifier.padding(start = 8.dp))
  }
}

@Composable
private fun UnitOption(
  title: String,
  hint: String,
  selected: Boolean,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    RadioButton(selected = selected, onClick = onClick)
    Column(modifier = Modifier.padding(start = 8.dp)) {
      Text(title)
      Text(hint, style = MaterialTheme.typography.bodySmall)
    }
  }
}
