package com.lightricks.feedexercise.data

import com.lightricks.feedexercise.database.FeedItemDao
import com.lightricks.feedexercise.database.toFeedItem
import com.lightricks.feedexercise.network.FeedApiService
import com.lightricks.feedexercise.network.toFeedItem
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers


/**
 * This is our data layer abstraction. Users of this class don't need to know
 * where the data actually comes from (network, database or somewhere else).
 */
class FeedRepositoryImpl(
    private val feedApiService: FeedApiService,
    private val feedItemsDao: FeedItemDao
) : FeedRepository {
    //todo: implement
    override fun getFeedItems(): Observable<List<FeedItem>> =
        feedItemsDao.getAll().map { entities ->
            entities.map {
                it.toFeedItem()
            }
        }

    override fun fetchFeed(): Completable {
        return feedApiService.getFeed()
            .subscribeOn(Schedulers.io())
            .map { response -> response.templatesMetadata.map { it.toFeedItem() } }
            .flatMapCompletable {
                feedItemsDao.insertAll(
                    it.map { feedItem -> feedItem.toFeedItemEntity() })
            }
    }
}
