package com.example.simplephotoapp.data.domain

import android.os.Parcelable
import com.example.simplephotoapp.database.PhotoEntity
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
{
    fun getDatabasePhoto(): PhotoEntity{
        return PhotoEntity(
            this.id,
            this.photoUrl,
            this.creatorNickname,
            this.imageDescription,
            this.photoLikes,
            this.photoUrlDownload
        )
    }
}


