package com.rasel.flickergallery.data.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.*

@JsonClass(generateAdapter = true)
data class ImageListResponse(
    val items: List<Item>,
    val modified: String,
    val title: String
)

@kotlinx.parcelize.Parcelize
@JsonClass(generateAdapter = true)
data class Item(
    val author: String,
    @Json(name = "author_id")
    val authorId: String,
    @Json(name = "date_taken")
    val dateTaken: Date,
    val description: String,
    val link: String,
    val media: Media,
    val published: Date,
    val tags: String,
    val title: String
) : Parcelable  {
    val formattedTakenDate: String
        get() {
            return SimpleDateFormat("d MMMM, yyyy (hh:mm a)", Locale.getDefault()).format(dateTaken)
        }

    val formattedPublishedDate: String
        get() {
            return SimpleDateFormat("d MMMM, yyyy (hh:mm a)", Locale.getDefault()).format(published)
        }
}

@kotlinx.parcelize.Parcelize
@JsonClass(generateAdapter = true)
data class Media(
    val m: String
) : Parcelable