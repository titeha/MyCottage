package ru.mycottege.app.domain.crops

data class CropSpec(
  val id: CropId,
  val daysToHarvestMin: Int,
  val daysToHarvestMax: Int
)
