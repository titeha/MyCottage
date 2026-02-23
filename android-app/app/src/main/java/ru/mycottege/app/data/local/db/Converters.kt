package ru.mycottege.app.data.local.db

import androidx.room.TypeConverter
import java.time.LocalDate

// Конвертеры для Room (LocalDate <-> Long)
class Converters {

  @TypeConverter
  fun localDateToEpochDay(date: LocalDate?): Long? = date?.toEpochDay()

  @TypeConverter
  fun epochDayToLocalDate(value: Long?): LocalDate? = value?.let(LocalDate::ofEpochDay)
}
