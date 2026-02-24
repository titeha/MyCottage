package ru.mycottege.app.domain.events

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import ru.mycottege.app.domain.crops.CropId
import ru.mycottege.app.domain.plantings.PlantingSnapshot
import java.time.LocalDate

class HarvestEventsGeneratorTest {

  @Test
  fun generates_active_and_upcoming_events_and_sorts_them() {
    val today = LocalDate.of(2026, 5, 10)

    // Редис: 20–30 дней
    // Посадили 25 дней назад => окно 5–15 мая => 10 мая ACTIVE
    val radishActive = PlantingSnapshot(
      id = 1,
      cropId = CropId.RADISH,
      cropNameFallback = "Редис",
      plantedDate = today.minusDays(25)
    )

    // Огурец: 40–55 дней
    // Посадили 10 дней назад => окно начнётся через 30 дней => не попадает в горизонт 30? (ровно попадает)
    val cucumberUpcoming = PlantingSnapshot(
      id = 2,
      cropId = CropId.CUCUMBER,
      cropNameFallback = "Огурец",
      plantedDate = today.minusDays(10)
    )

    val events = HarvestEventsGenerator.generate(
      plantings = listOf(cucumberUpcoming, radishActive),
      today = today,
      horizonDays = 30
    )

    // Должно быть 2 события: ACTIVE + UPCOMING
    assertEquals(2, events.size)

    // Первое — ACTIVE (sortDate=today)
    assertEquals(HarvestEventStatus.ACTIVE, events[0].status)
    assertTrue(events[0].sortDate == today)
  }
}
