package ru.mycottege.app.data.local.db

import ru.mycottege.app.domain.crops.CropId
import ru.mycottege.app.domain.plantings.PlantingSnapshot

fun PlantingEntity.toSnapshot(): PlantingSnapshot {
  val parsedId = cropId?.let { runCatching { CropId.valueOf(it) }.getOrNull() }

  return PlantingSnapshot(
    id = id,
    cropId = parsedId,
    cropNameFallback = cropName,
    plantedDate = plantedDate
  )
}
