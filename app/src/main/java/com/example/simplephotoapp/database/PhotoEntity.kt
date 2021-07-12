package com.example.simplephotoapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.simplephotoapp.data.domain.PhotoDomain

@Entity
data class PhotoEntity constructor(
    @PrimaryKey
    val id: String,
    val photoUrl: String,
    val creatorNickname: String,
    val imageDescription: String?,
    val photoLikes: Int?,
    val photoDownloadUrl: String
)

fun List<PhotoEntity>.asDomainModel() : List<PhotoDomain>{
    return map {
        PhotoDomain(
            id = it.id,
            photoUrl = it.photoUrl,
            creatorNickname = it.creatorNickname,
            imageDescription = it.imageDescription,
            photoLikes = it.photoLikes,
            photoUrlDownload = it.photoDownloadUrl
        )
    }
}