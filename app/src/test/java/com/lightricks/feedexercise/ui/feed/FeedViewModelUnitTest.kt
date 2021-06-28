package com.lightricks.feedexercise.ui.feed

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.lightricks.feedexercise.data.FeedItem
import com.lightricks.feedexercise.data.FeedRepositoryImpl
import com.lightricks.feedexercise.ui.feed.util.testObserver
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.times
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class FeedViewModelUnitTest {
    @get:Rule
    var instantLiveData: TestRule = InstantTaskExecutorRule()

    private lateinit var testedFeedViewModel: FeedViewModel
    private lateinit var mockFeedRepository: FeedRepositoryImpl
    private val templateFeedItems = arrayListOf(
        FeedItem(id = "id 1", thumbnailUrl = "url 1", isPremium = true),
        FeedItem(id = "id 2", thumbnailUrl = "url 2", isPremium = true)
    )

    @Before
    fun setup() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setSingleSchedulerHandler { Schedulers.trampoline() }

        mockFeedRepository = Mockito.mock(FeedRepositoryImpl::class.java).apply {
            `when`(fetchFeed()).thenReturn(Completable.complete())
            `when`(getFeedItems()).thenReturn(Observable.just(templateFeedItems))
        }
        testedFeedViewModel = FeedViewModel(mockFeedRepository)
    }

    @Test
    fun getFeedItems_afterRefresh_shouldReturnUpdatedFeed() {
        testedFeedViewModel.refresh()
        val value: List<FeedItem>? =
            testedFeedViewModel.getFeedItems().testObserver().getCurrentValue()

        assertThat(value).isEqualTo(templateFeedItems)
    }

    @Test
    fun getFeedItems_whenRepositoryFeedChanged_shouldReturnFeedWithChanges() {
        templateFeedItems.add(FeedItem(id = "id 3", thumbnailUrl = "url 3", isPremium = false))
        val currentValue = testedFeedViewModel.getFeedItems().testObserver().getCurrentValue()

        assertThat(currentValue).isEqualTo(templateFeedItems)
    }

    @Test
    fun refresh_whenCalled_shouldCallRepositoryFetchFeed() {
        Mockito.reset(mockFeedRepository)
        mockFeedRepository.apply {
            `when`(fetchFeed()).thenReturn(Completable.complete())
        }

        testedFeedViewModel.refresh()

        Mockito.verify(mockFeedRepository, times(1)).fetchFeed()
    }
}
