package com.lightricks.feedexercise.network

import io.reactivex.rxjava3.core.Single

/**
 * todo: implement the mock feed API service here
 */

class MockFeedApiService : FeedApiService {
    override fun getFeed(): Single<GetFeedResponse> {
        val item1 = TemplateMetadataItem(
            configuration = "configuration 1",
            id = "id 1",
            isNew = true,
            isPremium = true,
            templateCategories = emptyList(),
            templateName = "templateName1",
            templateThumbnailURI = "templateThumbnailURI 1"
        )
        val item2 = TemplateMetadataItem(
            configuration = "configuration 2",
            id = "id 2",
            isNew = true,
            isPremium = true,
            templateCategories = emptyList(),
            templateName = "templateName2",
            templateThumbnailURI = "templateThumbnailURI 2"
        )
        val feedResponse = GetFeedResponse(listOf(item1, item2))
        return Single.just(feedResponse)
    }

}