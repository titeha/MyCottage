package ru.mycottege.app.data.local.db

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class ConvertersTest {

  @Test
  fun localDate_roundtrip() {
    val c = Converters()
    val date = LocalDate.of(2026, 5, 1)

    val stored = c.localDateToEpochDay(date)
    val restored = c.epochDayToLocalDate(stored)

    assertEquals(date, restored)
  }
}
