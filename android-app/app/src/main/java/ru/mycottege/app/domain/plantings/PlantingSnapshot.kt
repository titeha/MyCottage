package ru.mycottege.app.domain.plantings

import ru.mycottege.app.domain.crops.CropId
import java.time.LocalDate

data class PlantingSnapshot(
  val id: Long,
  val cropId: CropId?,
  val cropNameFallback: String,
  val plantedDate: LocalDate
)
