package com.lightricks.feedexercise.data

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface FeedRepository {
    fun getFeedItems(): Observable<List<FeedItem>>
    fun fetchFeed(): Completable
}
