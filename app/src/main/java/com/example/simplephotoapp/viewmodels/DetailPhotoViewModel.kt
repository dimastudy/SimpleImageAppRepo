package com.example.simplephotoapp.viewmodels

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.simplephotoapp.data.PhotoRepository
import com.example.simplephotoapp.data.domain.PhotoDomain
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class DetailPhotoViewModel @AssistedInject constructor(
    private val repository: PhotoRepository,
    @Assisted private val photo: PhotoDomain
) : ViewModel() {


    private val _photo = MutableLiveData<PhotoDomain>()
    val photoProperty: LiveData<PhotoDomain>
        get() = _photo

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean>
        get() = _isFavorite

    private val _downloadPhoto = MutableLiveData<Boolean>()
    val downloadPhoto: LiveData<Boolean>
        get() = _downloadPhoto

    private val _downloadId = MutableLiveData<Long?>()
    val downloadId: LiveData<Long?>
        get() = _downloadId

    init {
        _downloadPhoto.value = false
        _photo.value = photo
        _downloadId.value = null
        isFavoriteInit()
    }


    fun addPhotoToFavorite(photo: PhotoDomain) {
        viewModelScope.launch {
            if (!repository.isPhotoFavorite(photo.id)) {
                repository.insertPhotoToDatabase(photo)
                _isFavorite.value = true
            }
        }
    }

    fun isFavoriteInit() {
        viewModelScope.launch {
            _isFavorite.value = repository.isPhotoFavorite(photoProperty.value!!.id)
        }
    }

    fun deleteFavoritePhoto(photo: PhotoDomain) {
        viewModelScope.launch {
            if (repository.isPhotoFavorite(photo.id)) {
                repository.deletePhotoFromDatabase(photo)
                _isFavorite.value = false
            }
        }
    }

    private fun verifyPermission(activity: Activity): Boolean {

        val permissionExternalMemory: Int =
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {
            val STORAGE_PERMISSIONS = listOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(activity, STORAGE_PERMISSIONS.toTypedArray(), 1)
            return false
        }

        return true
    }


    fun beginDownload(imageUrl: String, activity: Activity) {
        val filename = "${System.currentTimeMillis()}.jpg"
//        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), filename)

        var request: DownloadManager.Request? = null
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            request = DownloadManager.Request(Uri.parse(imageUrl))
                .setTitle("Image")
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, filename)
//                .setDestinationUri(Uri.fromFile(file))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        } else {
            request = DownloadManager.Request(Uri.parse(imageUrl))
                .setTitle("Image")
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, filename)
                .setAllowedOverRoaming(true)
        }

        val downloadManager = activity.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        _downloadId.value = downloadManager.enqueue(request)

    }


    companion object {
        fun provideFactory(
            assistedFactory: AssistedFactory,
            photo: PhotoDomain
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return assistedFactory.create(photo) as T
            }

        }
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(photo: PhotoDomain): DetailPhotoViewModel
    }


}
