package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Task::class,
        Note::class,
        Flashcard::class,
        StudySession::class,
        MindMapNode::class,
        UserProfile::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VibeDatabase : RoomDatabase() {
    abstract fun vibeDao(): VibeDao

    companion object {
        @Volatile
        private var INSTANCE: VibeDatabase? = null

        fun getDatabase(context: Context): VibeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VibeDatabase::class.java,
                    "vibestudy_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
