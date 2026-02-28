package ru.mycottege.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SiteDao {

  @Query("SELECT * FROM sites ORDER BY name")
  fun observeAll(): Flow<List<SiteEntity>>

  @Query("SELECT * FROM sites WHERE id = :id LIMIT 1")
  suspend fun getById(id: Long): SiteEntity?

  @Insert
  suspend fun insert(entity: SiteEntity): Long
}
