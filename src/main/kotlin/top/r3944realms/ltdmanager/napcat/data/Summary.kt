package top.r3944realms.ltdmanager.napcat.data

import kotlinx.serialization.Serializable

@Serializable
data class Summary (
    val audioSummary: String,
    val fileSummary: String,
    val gallerySummary: String,
    val linkSummary: String,
    val locationSummary: String,
    val richMediaSummary: String,
    val textSummary: String,
    val videoSummary: String
)
