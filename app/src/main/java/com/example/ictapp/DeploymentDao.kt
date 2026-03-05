package com.example.ictapp

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DeploymentDao {
    @Query("SELECT * FROM deployment_tasks ORDER BY date DESC")
    fun getAllTasks(): Flow<List<DeploymentTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: DeploymentTask)

    @Update
    suspend fun updateTask(task: DeploymentTask)

    @Delete
    suspend fun deleteTask(task: DeploymentTask)

    @Query("DELETE FROM deployment_tasks WHERE status = 'Completed'")
    suspend fun deleteAllCompleted()
}
