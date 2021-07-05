package com.lightricks.feedexercise.network

import com.lightricks.feedexercise.data.FeedItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * todo: add Data Transfer Object data class(es) here
 */

@JsonClass(generateAdapter = true)
data class TemplateMetadataItemDTO(
    @Json(name="configuration")
    val configuration: String,
    @Json(name="id")
    val id: String,
    @Json(name="isNew")
    val isNew: Boolean,
    @Json(name="isPremium")
    val isPremium: Boolean,
    @Json(name="templateCategories")
    val templateCategories: List<String>,
    @Json(name="templateName")
    val templateName: String,
    @Json(name="templateThumbnailURI")
    val templateThumbnailURI: String
)

@JsonClass(generateAdapter = true)
data class GetFeedResponseDTO(
    @Json(name="templatesMetadata")
    val templatesMetadata: List<TemplateMetadataItemDTO>
)
fun TemplateMetadataItemDTO.toFeedItem(): FeedItem {
    return FeedItem(
        id = id,
        thumbnailUrl = FeedApiService.THUMBNAIL_PREFIX + templateThumbnailURI,
        isPremium = isPremium
    )
}
