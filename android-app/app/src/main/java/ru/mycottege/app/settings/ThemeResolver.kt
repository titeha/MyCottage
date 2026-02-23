package ru.mycottege.app.settings

// Вычисляем, нужна ли тёмная тема, исходя из режима и состояния системы.
fun resolveDarkTheme(themeMode: ThemeMode, isSystemDark: Boolean): Boolean = when (themeMode) {
  ThemeMode.SYSTEM -> isSystemDark
  ThemeMode.LIGHT -> false
  ThemeMode.DARK -> true
}
