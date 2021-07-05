package com.lightricks.feedexercise.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * todo: add the abstract class that extents RoomDatabase here
 */

@Database(entities = [FeedItemEntity::class], version = 1)
abstract class FeedRoomDatabase : RoomDatabase() {
    abstract fun feedItemsDao(): FeedItemDao

    companion object {
        @Volatile
        private var db: FeedRoomDatabase? = null
        fun getInstance(context: Context): FeedRoomDatabase {
            return db  ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    FeedRoomDatabase::class.java,
                    "feed_database"
                ).build()
                db = instance

                instance
            }
        }
    }
}
