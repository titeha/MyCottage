package ru.mycottege.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ru.mycottege.app.ui.AppRoot
import ru.mycottege.app.ui.theme.МояДачаTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      МояДачаTheme {
        AppRoot()
      }
    }
  }
}
