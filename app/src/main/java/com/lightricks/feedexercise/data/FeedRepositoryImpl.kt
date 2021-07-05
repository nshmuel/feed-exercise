package com.lightricks.feedexercise.data

import com.lightricks.feedexercise.network.FeedApiService
import com.lightricks.feedexercise.network.toFeedItem
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject

/**
 * This is our data layer abstraction. Users of this class don't need to know
 * where the data actually comes from (network, database or somewhere else).
 */
class FeedRepositoryImpl(private val feedApiService: FeedApiService) :FeedRepository{
    //todo: implement
    private val feedItems = BehaviorSubject.createDefault<List<FeedItem>>(emptyList())

    override fun getFeedItems(): Observable<List<FeedItem>> = feedItems

    override fun fetchFeed(): Completable {
        return feedApiService.getFeed()
            .subscribeOn(Schedulers.io())
            .map { response -> response.templatesMetadata.map { it.toFeedItem() } }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { items -> feedItems.onNext(items) }
            .ignoreElement()
    }
}
