package com.lightricks.feedexercise.data

import android.accounts.NetworkErrorException
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.lightricks.feedexercise.network.FeedApiService
import com.lightricks.feedexercise.network.GetFeedResponseDTO
import com.lightricks.feedexercise.network.TemplateMetadataItemDTO
import com.lightricks.feedexercise.network.toFeedItem
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
class FeedRepositoryImplTest {

    @get:Rule
    var instantLiveData: TestRule = InstantTaskExecutorRule()

    //todo: add the tests here
    private lateinit var feedRepositoryImpl: FeedRepositoryImpl
    private lateinit var mockFeedApiService: FeedApiService

    @Before
    fun setup() {
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setSingleSchedulerHandler { Schedulers.trampoline() }

        mockFeedApiService = mock(FeedApiService::class.java)

        feedRepositoryImpl = FeedRepositoryImpl(mockFeedApiService)
    }

    @Test
    fun getFeedItems_whileFetchedFeedSucceeded_shouldReturnFetchedData() {
        mockFeedApiService.apply {
            `when`(getFeed()).thenReturn(Single.just(GetFeedResponseDTO(feedItems)))
        }
        val expected = feedItems.map { it.toFeedItem() }

        feedRepositoryImpl.fetchFeed().test().assertComplete()

        val result = feedRepositoryImpl.getFeedItems().test().values()
        Truth.assertThat(result.count()).isEqualTo(1)
        Truth.assertThat(result.first()).isEqualTo(expected)
    }

    @Test
    fun fetchFeed_whenFetchFailed_shouldThrowException() {
        mockFeedApiService.apply {
            `when`(getFeed()).thenReturn(Single.error(NetworkErrorException()))
        }

        feedRepositoryImpl.fetchFeed().test().assertError(NetworkErrorException::class.java)
    }

    @Test
    fun fetchFeed_whenFetchingData_shouldBeUsingTheApiService() {
        mockFeedApiService.apply {
            `when`(getFeed()).thenReturn(Single.just(GetFeedResponseDTO(arrayListOf())))
        }
        feedRepositoryImpl.fetchFeed().test().assertComplete()

        verify(mockFeedApiService, times(1)).getFeed()
    }

    companion object {
        val feedItems = arrayListOf(
            TemplateMetadataItemDTO(
                configuration = "configuration 1",
                id = "id 1",
                isNew = true,
                isPremium = true,
                templateCategories = emptyList(),
                templateName = "templateName1",
                templateThumbnailURI = "templateThumbnailURI 1"
            ),
            TemplateMetadataItemDTO(
                configuration = "configuration 2",
                id = "id 2",
                isNew = true,
                isPremium = true,
                templateCategories = emptyList(),
                templateName = "templateName2",
                templateThumbnailURI = "templateThumbnailURI 2"
            )
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
