package com.example.ictapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [RepairLog::class, DeploymentTask::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repairDao(): RepairDao
    abstract fun deploymentDao(): DeploymentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ict_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
