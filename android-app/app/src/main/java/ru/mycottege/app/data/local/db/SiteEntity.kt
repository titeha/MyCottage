package ru.mycottege.app.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sites")
data class SiteEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String
)
