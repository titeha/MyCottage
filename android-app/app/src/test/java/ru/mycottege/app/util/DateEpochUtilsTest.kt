package ru.mycottege.app.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DateEpochUtilsTest {

  @Test
  fun roundtrip_localDate_to_utcMillis_and_back() {
    val date = LocalDate.of(2026, 5, 1)
    val millis = localDateToUtcEpochMillis(date)
    val restored = utcEpochMillisToLocalDate(millis)
    assertEquals(date, restored)
  }
}
