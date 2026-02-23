package ru.mycottege.app.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "plantings")
data class PlantingEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val cropName: String,
  val plantedDate: LocalDate
)
