package com.lightricks.feedexercise.data

import android.accounts.NetworkErrorException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.lightricks.feedexercise.network.FeedApiService
import com.lightricks.feedexercise.network.GetFeedResponse
import com.lightricks.feedexercise.network.MockFeedApiService
import com.lightricks.feedexercise.network.TemplateMetadataItem
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class FeedRepositoryTest {

    @get:Rule
    var instantLiveData: TestRule = InstantTaskExecutorRule()

    //todo: add the tests here
    private lateinit var feedRepository: FeedRepository
    private lateinit var mockFeedApiService: FeedApiService

    @Before
    fun setup() {
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setSingleSchedulerHandler { Schedulers.trampoline() }

        mockFeedApiService = MockFeedApiService()
        feedRepository = FeedRepository(mockFeedApiService)
    }

    @Test
    fun verifyReturnTheFetchedData() {
        val testedFeedRepository = FeedRepository(mockFeedApiService)
        val trueFeed = arrayListOf<FeedItem>()
        val receivedList = arrayListOf<FeedItem>()
        mockFeedApiService.getFeed().subscribe(
            { response ->
                response
                    .templatesMetadata.map { it.toFeedItem() }
                    .apply { trueFeed.addAll(this) }
            }, {})

        testedFeedRepository.fetchFeed().subscribe()
        testedFeedRepository.getFeedItems().subscribe { receivedList.addAll(it) }

        Truth.assertThat(receivedList).isEqualTo(trueFeed)
    }

    @Test
    fun verifyReturnErrorWhenFetchFailed() {
        val mockService = mock(FeedApiService::class.java)
        `when`(mockService.getFeed()).thenReturn(Single.error(NetworkErrorException()))
        val testedFeedRepository = FeedRepository(mockService)
        testedFeedRepository.fetchFeed().subscribe({},
            {
                Truth.assertThat(it).isInstanceOf(NetworkErrorException::class.java)
            })
    }

    @Test
    fun verifyUsingTheApiToFetchData() {
        val mockService = mock(FeedApiService::class.java)
        `when`(mockService.getFeed()).thenReturn(Single.just(GetFeedResponse(arrayListOf())))

        val testedFeedRepository = FeedRepository(mockService)
        testedFeedRepository.fetchFeed().subscribe()

        verify(mockService, times(1)).getFeed()
    }

    private fun TemplateMetadataItem.toFeedItem(): FeedItem {
        return FeedItem(
            id = id,
            thumbnailUrl = FeedApiService.THUMBNAIL_PREFIX + templateThumbnailURI,
            isPremium = isPremium
        )
    }
}

private fun <T> LiveData<T>.blockingObserve(): T? {
    var value: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        override fun onChanged(t: T) {
            value = t
            latch.countDown()
            removeObserver(this)
        }
    }

    observeForever(observer)
    latch.await(5, TimeUnit.SECONDS)
    return value
}
