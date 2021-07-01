package com.lightricks.feedexercise.ui.feed

import android.util.Log
import androidx.lifecycle.*
import com.lightricks.feedexercise.R
import com.lightricks.feedexercise.data.FeedItem
import com.lightricks.feedexercise.data.FeedRepositoryImpl
import com.lightricks.feedexercise.data.FeedRepository
import com.lightricks.feedexercise.network.FeedApiService
import com.lightricks.feedexercise.util.Event
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable

/**
 * This view model manages the data for [FeedFragment].
 */
open class FeedViewModel(private val repository: FeedRepository) : ViewModel() {

    private val stateLiveData = MutableLiveData<State>()
    private val networkErrorEvent = MutableLiveData<Event<String>>()
    private var prevRefreshFeedTask: Disposable? = null
    private var getFeedFromRepoStream: Disposable? = null

    fun getIsLoading(): LiveData<Boolean> = Transformations.map(stateLiveData) { it.isLoading }
    fun getIsEmpty(): LiveData<Boolean> = Transformations.map(stateLiveData) { it.isFeedEmpty() }
    fun getFeedItems(): LiveData<List<FeedItem>> =
        Transformations.map(stateLiveData) { it.feedItems }

    fun getNetworkErrorEvent(): LiveData<Event<String>> = networkErrorEvent

    init {
        observeRepository()
        refresh()
    }

    fun refresh() {
        disposePrevRefreshFeedTask()
        setStartLoading()
        prevRefreshFeedTask = repository.fetchFeed()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ setStoppedLoading() },
                { error ->
                    handleNetworkError(error)
                    setStoppedLoading()
                })
    }

    private fun disposePrevRefreshFeedTask() {
        prevRefreshFeedTask?.dispose()
        prevRefreshFeedTask = null
    }

    private fun handleNetworkError(error: Throwable?) {
        networkErrorEvent.value = Event(R.string.repository_network_error.toString())
        Log.e(TAG, "Failed to fetch feed from network", error)
    }

    private fun observeRepository() {
        getFeedFromRepoStream = repository.getFeedItems().subscribe({ items ->
            stateLiveData.value = when (stateLiveData.value) {
                null -> State(feedItems = items)
                else -> stateLiveData.value!!.copy(feedItems = items)
            }
        }, { error -> Log.e(TAG, "Failed to get feed from repository", error) })
    }

    private fun setStartLoading() {
        stateLiveData.value = stateLiveData.value!!.copy(isLoading = true)
    }

    private fun setStoppedLoading() {
        stateLiveData.value = stateLiveData.value!!.copy(isLoading = false)
    }

    data class State(
        val isLoading: Boolean = false,
        val feedItems: List<FeedItem> = emptyList()
    ) {
        fun isFeedEmpty(): Boolean {
            return feedItems.isEmpty()
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposePrevRefreshFeedTask()
        disposeFeedStream()
    }

    private fun disposeFeedStream() {
        getFeedFromRepoStream?.dispose()
    }

    companion object {
        private const val TAG = "FeedViewModel"
    }
}

/**
 * This class creates instances of [FeedViewModel].
 * It's not necessary to use this factory at this stage. But if we will need to inject
 * dependencies into [FeedViewModel] in the future, then this is the place to do it.
 */
class FeedViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            throw IllegalArgumentException("factory used with a wrong class")
        }
        @Suppress("UNCHECKED_CAST")
        return FeedViewModel(FeedRepositoryImpl(FeedApiService.instance)) as T
    }
}
