package ru.mycottege.app.data.local.db

import android.content.Context
import androidx.room.Room

// Простой синглтон БД. Позже можно заменить на DI.
object DbProvider {

  @Volatile private var instance: AppDatabase? = null

  fun get(context: Context): AppDatabase {
    return instance ?: synchronized(this) {
      instance ?: Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "my_cottage.db"
      )
        .addMigrations(MIGRATION_1_2)
        .fallbackToDestructiveMigration()
        .build()
        .also { instance = it }
    }
  }
}
