package com.example.simplephotoapp.viewmodels

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.simplephotoapp.R
import com.example.simplephotoapp.data.PhotoRepository
import com.example.simplephotoapp.data.domain.PhotoDomain
import com.example.simplephotoapp.data.models.Photo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.lang.IllegalArgumentException
import javax.inject.Inject


class DetailPhotoViewModel (
    private val repository: PhotoRepository,
    private val photo: PhotoDomain
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

    init {
        _downloadPhoto.value = false
        _photo.value = photo
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


    fun saveMediaToStorage(bitmap: Bitmap, activity: Activity) {
        //Generating a file name
        val filename = "${System.currentTimeMillis()}.jpg"

        //Output stream
        var fos: OutputStream? = null

        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            activity.contentResolver?.also { resolver ->

                //Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                //Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val imagesDir =
//                activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    _downloadPhoto.value = false
                }
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


    fun downloadImage(imageUrl: String, activity: Activity) {
        GlobalScope.launch(Dispatchers.IO) {
            if (!verifyPermission(activity)) {
                return@launch
            }
            withContext(Dispatchers.Main) {
                _downloadPhoto.value = true
            }

            Glide.with(activity)
                .asBitmap()
                .load(imageUrl)
                .into(object : SimpleTarget<Bitmap>(1920, 1080) {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        val bitmap: Bitmap = resource
                        saveMediaToStorage(bitmap, activity)
                    }
                })

        }
    }

    class Factory @AssistedInject constructor(
        @Assisted("photoDomain") val photo: PhotoDomain,
        private val repository: PhotoRepository
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailPhotoViewModel::class.java)){
                return DetailPhotoViewModel(repository, photo) as T
            }
            throw IllegalArgumentException("Unknown viewModel class")
        }

        @AssistedFactory
        interface Factory {
            fun create(@Assisted("photoDomain") photo: PhotoDomain) : DetailPhotoViewModel.Factory
        }

    }

}