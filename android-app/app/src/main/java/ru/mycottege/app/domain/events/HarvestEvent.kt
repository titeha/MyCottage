package ru.mycottege.app.domain.events

import ru.mycottege.app.domain.crops.CropId
import java.time.LocalDate

data class HarvestEvent(
  val plantingId: Long,
  val cropId: CropId,
  val cropNameFallback: String,
  val status: HarvestEventStatus,
  val windowStart: LocalDate,
  val windowEnd: LocalDate,
  val sortDate: LocalDate
)
