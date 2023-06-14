package com.hamzafrd.storia.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hamzafrd.storia.data.local.entity.RemoteKeysEntity
import com.hamzafrd.storia.data.local.entity.StoryEntity

@Database(
    entities = [StoryEntity::class, RemoteKeysEntity::class],
    version = 2,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var instance: StoryDatabase? = null
        fun getInstance(context: Context): StoryDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoryDatabase::class.java, "stories.db"
                ).build()
            }
    }
}