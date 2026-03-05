package com.example.ictapp

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RepairDao {
    @Query("SELECT * FROM repair_logs ORDER BY date DESC")
    fun getAllLogs(): Flow<List<RepairLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: RepairLog)

    @Update
    suspend fun updateLog(log: RepairLog)

    @Delete
    suspend fun deleteLog(log: RepairLog)
}
