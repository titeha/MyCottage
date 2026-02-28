package ru.mycottege.app.domain.cultivation

import org.junit.Assert.assertEquals
import org.junit.Test

class CultivationMethodTest {

  @Test
  fun fromStorage_null_defaults_openGround() {
    assertEquals(CultivationMethod.OPEN_GROUND, CultivationMethod.fromStorage(null))
  }

  @Test
  fun fromStorage_unknown_defaults_openGround() {
    assertEquals(CultivationMethod.OPEN_GROUND, CultivationMethod.fromStorage("NOPE"))
  }

  @Test
  fun fromStorage_parses() {
    assertEquals(CultivationMethod.GREENHOUSE, CultivationMethod.fromStorage("GREENHOUSE"))
  }
}
