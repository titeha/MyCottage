package ru.mycottege.app.domain.harvest

import ru.mycottege.app.domain.crops.CropSpec
import java.time.LocalDate

object HarvestWindowCalculator {
  fun calculate(plantedDate: LocalDate, crop: CropSpec): HarvestWindow {
    val start = plantedDate.plusDays(crop.daysToHarvestMin.toLong())
    val end = plantedDate.plusDays(crop.daysToHarvestMax.toLong())
    return HarvestWindow(start, end)
  }
}
