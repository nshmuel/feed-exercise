package com.lightricks.feedexercise.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.reactivex.rxjava3.core.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

/**
 * todo: add the FeedApiService interface and the Retrofit and Moshi code here
 */

interface FeedApiService {
    @GET(value = "/Android/demo/feed.json")
    fun getFeed(): Single<GetFeedResponseDTO>

    companion object{
        private const val BASE_URL = "https://assets.swishvideoapp.com"
        const val THUMBNAIL_PREFIX  = "$BASE_URL/Android/demo/catalog/thumbnails/"

        val instance by lazy { create() }

         private fun create(): FeedApiService {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()

            return retrofit.create(FeedApiService::class.java)
        }
    }
}
