package com.lightricks.feedexercise.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

/***
 * todo: add Room's Data Access Object interface(s) here
 */

@Dao
interface FeedItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(feedItems: List<FeedItemEntity>): Completable

    @Query("DELETE FROM FeedItems")
    fun deleteAll(): Completable

    @Query("SELECT * FROM FeedItems")
    fun getAll(): Observable<List<FeedItemEntity>>

    @Query("SELECT COUNT(id) FROM FeedItems")
    fun getItemsCountSync(): Int
}
