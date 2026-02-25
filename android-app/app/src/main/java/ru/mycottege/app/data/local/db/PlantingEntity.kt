package ru.mycottege.app.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "plantings")
data class PlantingEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val cropId: String? = null,
  val cropName: String,

  // Название сорта/гибрида (опционально). Пока просто текст.
  val varietyName: String? = null,

  val plantedDate: LocalDate
)
