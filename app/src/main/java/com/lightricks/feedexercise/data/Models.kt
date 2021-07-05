package com.lightricks.feedexercise.data

import com.lightricks.feedexercise.database.FeedItemEntity

/**
 * This data class is our internal representation of a feed item.
 * In a real-life application this is template meta-data for a user's project.
 * The rest of the properties are left out for brevity.
 */
data class FeedItem(
    val id: String, // Kotlin note: "val" means read-only value.
    val thumbnailUrl: String,
    val isPremium: Boolean
)

fun FeedItem.toFeedItemEntity(): FeedItemEntity {
    return FeedItemEntity(id = id, thumbnailUrl = thumbnailUrl, isPremium = isPremium)
}
