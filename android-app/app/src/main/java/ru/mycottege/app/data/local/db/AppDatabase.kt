package ru.mycottege.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
  entities = [PlantingEntity::class, SiteEntity::class],
  version = 4,
  exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun plantingDao(): PlantingDao
  abstract fun siteDao(): SiteDao
}
