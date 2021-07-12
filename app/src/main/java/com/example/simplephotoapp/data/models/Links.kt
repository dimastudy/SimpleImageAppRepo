package com.example.simplephotoapp.data.models


import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize


@Parcelize
data class Links(
    @Json(name = "download")
    val download: String,
    @Json(name = "html")
    val html: String,
    @Json(name = "self")
    val self: String
) : Parcelable