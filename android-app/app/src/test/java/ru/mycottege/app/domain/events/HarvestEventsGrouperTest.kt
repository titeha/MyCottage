package ru.mycottege.app.domain.events

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.mycottege.app.domain.crops.CropId
import java.time.LocalDate

class HarvestEventsGrouperTest {

  @Test
  fun groups_by_date_and_sorts_active_first() {
    val d1 = LocalDate.of(2026, 5, 10)
    val d2 = LocalDate.of(2026, 5, 15)

    val e1 = HarvestEvent(
      plantingId = 1,
      cropId = CropId.RADISH,
      cropNameFallback = "Редис",
      status = HarvestEventStatus.UPCOMING,
      windowStart = d2,
      windowEnd = d2.plusDays(10),
      sortDate = d2
    )

    val e2 = HarvestEvent(
      plantingId = 2,
      cropId = CropId.TOMATO,
      cropNameFallback = "Томат",
      status = HarvestEventStatus.ACTIVE,
      windowStart = d1.minusDays(2),
      windowEnd = d1.plusDays(5),
      sortDate = d1
    )

    val e3 = HarvestEvent(
      plantingId = 3,
      cropId = CropId.CUCUMBER,
      cropNameFallback = "Огурец",
      status = HarvestEventStatus.UPCOMING,
      windowStart = d1.plusDays(1),
      windowEnd = d1.plusDays(10),
      sortDate = d1
    )

    val days = HarvestEventsGrouper.groupByDate(listOf(e1, e2, e3))

    assertEquals(2, days.size)
    assertEquals(d1, days[0].date)
    assertEquals(d2, days[1].date)

    // Внутри d1 ACTIVE должен быть первым
    assertEquals(2, days[0].events.size)
    assertTrue(days[0].events[0].status == HarvestEventStatus.ACTIVE)
  }
}
