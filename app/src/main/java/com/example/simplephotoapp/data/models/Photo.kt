package com.example.simplephotoapp.data.models


import android.os.Parcelable
import com.example.simplephotoapp.data.domain.PhotoDomain
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class Photo(
    @Json(name = "description")
    val description: String?,
    @Json(name = "id")
    val id: String,
    @Json(name = "likes")
    val likes: Int?,
    @Json(name = "links")
    val links: Links,
    @Json(name = "urls")
    val urls: Urls,
    @Json(name = "user")
    val user: User,
    @Json(name = "width")
    val width: Int
): Parcelable{

    fun getPhotoDomain(): PhotoDomain{
        return PhotoDomain(
            this.id,
            this.urls.full,
            this.user.username,
            this.description,
            this.likes,
            this.links.download
        )
    }


}