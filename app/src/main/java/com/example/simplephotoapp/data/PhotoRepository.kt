package com.example.simplephotoapp.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.example.simplephotoapp.data.PhotoPagingSource
import com.example.simplephotoapp.data.domain.PhotoDomain
import com.example.simplephotoapp.data.models.Photo
import com.example.simplephotoapp.database.PhotoDatabase
import com.example.simplephotoapp.database.PhotoEntity
import com.example.simplephotoapp.database.asDomainModel
import com.example.simplephotoapp.database.getDatabase
import com.example.simplephotoapp.network.UnsplashApiService
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    private val photoApi: UnsplashApiService,
    private val database: PhotoDatabase
) {


    fun getPhoto(query: String) =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PhotoPagingSource(photoApi, query) }
        ).liveData


    val photos: LiveData<List<PhotoDomain>> =
        Transformations.map(database.photoDao.getPhotos()) { photoList ->
            photoList.asDomainModel()
        }

    suspend fun insertPhotoToDatabase(photo: PhotoDomain) {
        val photoEntity = PhotoEntity(
            photo.id,
            photo.photoUrl,
            photo.creatorNickname,
            photo.imageDescription,
            photo.photoLikes,
            photo.photoUrlDownload
        )
        database.photoDao.insertPhoto(photoEntity)
    }

    suspend fun deletePhotoFromDatabase(photo: PhotoDomain){
        val photoEntity = PhotoEntity(
            photo.id,
            photo.photoUrl,
            photo.creatorNickname,
            photo.imageDescription,
            photo.photoLikes,
            photo.photoUrlDownload
        )
        database.photoDao.deletePhoto(photoEntity)
    }



    suspend fun isPhotoFavorite(photoId: String): Boolean {
        return database.photoDao.isPhotoFavorite(photoId) != null
    }


    suspend fun clearDatabase(){
        database.photoDao.clear()
    }

}
