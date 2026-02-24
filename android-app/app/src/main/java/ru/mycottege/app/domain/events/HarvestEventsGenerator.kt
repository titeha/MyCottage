package ru.mycottege.app.domain.events

import ru.mycottege.app.domain.crops.CropCatalog
import ru.mycottege.app.domain.harvest.HarvestWindowCalculator
import ru.mycottege.app.domain.plantings.PlantingSnapshot
import java.time.LocalDate

object HarvestEventsGenerator {

  fun generate(
    plantings: List<PlantingSnapshot>,
    today: LocalDate,
    horizonDays: Int
  ): List<HarvestEvent> {
    val horizonEnd = today.plusDays(horizonDays.toLong())

    val events = plantings.mapNotNull { p ->
      val cropId = p.cropId ?: return@mapNotNull null
      val spec = CropCatalog.get(cropId)
      val window = HarvestWindowCalculator.calculate(p.plantedDate, spec)

      // уже всё прошло
      if (window.end.isBefore(today)) return@mapNotNull null

      // сбор идёт сейчас
      if (!window.start.isAfter(today) && !window.end.isBefore(today)) {
        return@mapNotNull HarvestEvent(
          plantingId = p.id,
          cropId = cropId,
          cropNameFallback = p.cropNameFallback,
          status = HarvestEventStatus.ACTIVE,
          windowStart = window.start,
          windowEnd = window.end,
          sortDate = today
        )
      }

      // сбор начнётся в ближайшие N дней
      if (!window.start.isBefore(today) && !window.start.isAfter(horizonEnd)) {
        return@mapNotNull HarvestEvent(
          plantingId = p.id,
          cropId = cropId,
          cropNameFallback = p.cropNameFallback,
          status = HarvestEventStatus.UPCOMING,
          windowStart = window.start,
          windowEnd = window.end,
          sortDate = window.start
        )
      }

      null
    }

    return events.sortedWith(
      compareBy<HarvestEvent> { it.sortDate }
        .thenBy { it.cropId.name }
        .thenBy { it.plantingId }
    )
  }
}
