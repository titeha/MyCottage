package ru.mycottege.app.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "plantings")
data class PlantingEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,

  // Участок (на старте всегда 1)
  val siteId: Long = 1,

  val cropId: String? = null,
  val cropName: String,
  val varietyName: String? = null,

  // OPEN_GROUND / GREENHOUSE (храним строкой)
  val cultivation: String = "OPEN_GROUND",

  val plantedDate: LocalDate
)
