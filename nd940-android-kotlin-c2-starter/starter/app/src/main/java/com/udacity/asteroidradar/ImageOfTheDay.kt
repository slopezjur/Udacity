package com.udacity.asteroidradar

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageOfTheDay(
    val url: String,
    @Json(name = "media_type")
    val mediaType: String,
    val title: String
) : Parcelable