package ru.mycottege.app.domain.timeline

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.time.LocalDate

class TimelineRangeTest {

  @Test
  fun segment_is_clamped_to_range() {
    val range = TimelineRange(
      start = LocalDate.of(2026, 5, 1),
      end = LocalDate.of(2026, 5, 10)
    )

    // Сегмент частично слева от диапазона: 29.04–03.05 => видим 01.05–03.05
    val seg = segmentFor(
      range,
      start = LocalDate.of(2026, 4, 29),
      endInclusive = LocalDate.of(2026, 5, 3)
    )

    assertNotNull(seg)
    assertEquals(0, seg!!.offsetDays)
    assertEquals(3, seg.lengthDays) // 01,02,03
  }
}
