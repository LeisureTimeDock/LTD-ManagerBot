package top.r3944realms.ltdmanager.napcat.data

import kotlinx.serialization.Serializable

@Serializable
data class CollectionSearchList (
    val bottomTimeStamp: String,
    val collectionItemList: List<CollectionItemList>,
    val hasMore: Boolean
)