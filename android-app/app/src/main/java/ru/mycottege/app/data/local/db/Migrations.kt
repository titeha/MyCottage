package ru.mycottege.app.data.local.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
  override fun migrate(db: SupportSQLiteDatabase) {
    db.execSQL("ALTER TABLE plantings ADD COLUMN cropId TEXT")
  }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
  override fun migrate(db: SupportSQLiteDatabase) {
    db.execSQL("ALTER TABLE plantings ADD COLUMN varietyName TEXT")
  }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
  override fun migrate(db: SupportSQLiteDatabase) {
    db.execSQL(
      "CREATE TABLE IF NOT EXISTS `sites` (" +
        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
        "`name` TEXT NOT NULL" +
        ")"
    )
    db.execSQL("INSERT OR IGNORE INTO sites(`id`,`name`) VALUES(1,'Участок 1')")

    db.execSQL("ALTER TABLE plantings ADD COLUMN siteId INTEGER NOT NULL DEFAULT 1")
    db.execSQL("ALTER TABLE plantings ADD COLUMN cultivation TEXT NOT NULL DEFAULT 'OPEN_GROUND'")
  }
}
