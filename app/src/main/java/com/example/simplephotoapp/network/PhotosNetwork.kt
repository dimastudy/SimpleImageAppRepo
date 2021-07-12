package com.example.simplephotoapp.network

import android.os.Parcelable
import com.example.simplephotoapp.data.models.Photo
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotosNetwork(
    @Json(name = "total")
    val total: Int,
    @Json(name = "total_pages")
    val total_pages: Int,
    @Json(name = "results")
    val results: List<Photo>
) : Parcelable