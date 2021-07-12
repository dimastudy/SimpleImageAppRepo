package com.example.simplephotoapp.data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoDomain(
    val id: String,
    val photoUrl: String,
    val creatorNickname: String,
    val imageDescription: String?,
    val photoLikes: Int?,
    val photoUrlDownload: String
): Parcelable


