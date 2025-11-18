package com.medassist.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.util.Log

/**
 * Room Database for MedAssist
 * 
 * Provides local database storage for offline access.
 * Currently stores articles for offline reading.
 */
@Database(
    entities = [ArticleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun articleDao(): ArticleDao
    
    companion object {
        private const val TAG = "AppDatabase"
        private const val DATABASE_NAME = "medassist_database"
        
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Get database instance (Singleton pattern)
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration() // For development - clears DB on schema change
                    .build()
                
                INSTANCE = instance
                Log.d(TAG, "✅ Room database initialized: $DATABASE_NAME")
                instance
            }
        }
    }
}

