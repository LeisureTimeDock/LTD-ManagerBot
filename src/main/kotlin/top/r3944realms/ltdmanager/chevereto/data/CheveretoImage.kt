package top.r3944realms.ltdmanager.chevereto.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheveretoImage(
    val name: String,
    val extension: String,
    val size: Long,
    val width: Int,
    val height: Int,
    val date: String,
    @SerialName("date_gmt")
    val dateGmt: String,
    val title: String,
    val tags: List<String>? = emptyList(),
    val description: String? = null,
    val nsfw: Int,
    @SerialName("storage_mode")
    val storageMode: String,
    val md5: String,
    @SerialName("source_md5")
    val sourceMd5: String? = null,
    @SerialName("original_filename")
    val originalFilename: String,
    @SerialName("original_exifdata")
    val originalExifdata: String? = null,
    val views: Int,
    @SerialName("category_id")
    val categoryId: String? = null,
    val chain: Int,
    @SerialName("thumb_size")
    val thumbSize: Int,
    @SerialName("medium_size")
    val mediumSize: Int,
    @SerialName("frame_size")
    val frameSize: Int? = null,
    @SerialName("expiration_date_gmt")
    val expirationDateGmt: String? = null,
    val likes: Int,
    @SerialName("is_animated")
    val isAnimated: Int,
    @SerialName("is_approved")
    val isApproved: Int,
    @SerialName("is_360")
    val is360: Int,
    val duration: Int? = null,
    val type: String? = null,
    @SerialName("tags_string")
    val tagsString: String? = null,
    val file: File? = null,
    @SerialName("id_encoded")
    val idEncoded: String,
    val filename: String,
    val mime: String,
    val url: String,
    val ratio: Double? = null,
    @SerialName("size_formatted")
    val sizeFormatted: String,
    val frame: ImageThumb? = null,
    val image: ImageFile,
    val thumb: ImageThumb,
    @SerialName("url_frame")
    val urlFrame: String? = null,
    val medium: Medium? = null,
    @SerialName("duration_time")
    val durationTime: String? = null,
    @SerialName("url_viewer")
    val urlViewer: String,
    @SerialName("path_viewer")
    val pathViewer: String? = null,
    @SerialName("url_short")
    val urlShort: String,
    @SerialName("display_url")
    val displayUrl: String,
    @SerialName("display_width")
    val displayWidth: Int,
    @SerialName("display_height")
    val displayHeight: Int,
    @SerialName("views_label")
    val viewsLabel: String,
    @SerialName("likes_label")
    val likesLabel: String,
    @SerialName("how_long_ago")
    val howLongAgo: String,
    @SerialName("date_fixed_peer")
    val dateFixedPeer: String,
    @SerialName("title_truncated")
    val titleTruncated: String,
    @SerialName("title_truncated_html")
    val titleTruncatedHtml: String,
    @SerialName("is_use_loader")
    val isUseLoader: Boolean,
    @SerialName("display_title")
    val displayTitle: String? = null,
    @SerialName("delete_url")
    val deleteUrl: String
)









