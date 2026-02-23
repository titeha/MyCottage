package ru.mycottege.app.settings

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemeResolverTest {

  @Test
  fun system_uses_system_value() {
    assertTrue(resolveDarkTheme(ThemeMode.SYSTEM, true))
    assertFalse(resolveDarkTheme(ThemeMode.SYSTEM, false))
  }

  @Test
  fun light_always_false() {
    assertFalse(resolveDarkTheme(ThemeMode.LIGHT, true))
    assertFalse(resolveDarkTheme(ThemeMode.LIGHT, false))
  }

  @Test
  fun dark_always_true() {
    assertTrue(resolveDarkTheme(ThemeMode.DARK, true))
    assertTrue(resolveDarkTheme(ThemeMode.DARK, false))
  }
}
