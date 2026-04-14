package com.sfyc.countdownlist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerDao {

    @Query("SELECT * FROM timer_tasks ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<TimerEntity>>

    @Query("SELECT * FROM timer_tasks WHERE status = :status ORDER BY deadlineMs ASC")
    fun observeByStatus(status: String): Flow<List<TimerEntity>>

    @Query("SELECT * FROM timer_tasks WHERE id = :id")
    suspend fun getById(id: Long): TimerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TimerEntity): Long

    @Update
    suspend fun update(entity: TimerEntity)

    @Delete
    suspend fun delete(entity: TimerEntity)

    @Query("UPDATE timer_tasks SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)
}
