package ru.mycottege.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantingDao {

  @Query("SELECT * FROM plantings ORDER BY plantedDate DESC, id DESC")
  fun observeAll(): Flow<List<PlantingEntity>>

  @Insert
  suspend fun insert(entity: PlantingEntity): Long

  @Query("DELETE FROM plantings WHERE id = :id")
  suspend fun deleteById(id: Long)
}
