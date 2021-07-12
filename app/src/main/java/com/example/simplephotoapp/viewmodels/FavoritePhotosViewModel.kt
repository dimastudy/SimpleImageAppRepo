package com.example.simplephotoapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simplephotoapp.data.PhotoRepository
import com.example.simplephotoapp.data.domain.PhotoDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class FavoritePhotosViewModel @Inject constructor(
    private val repository: PhotoRepository
) : ViewModel() {



    val favoritePhotos = repository.photos

    private val _navigateToDetailPhoto = MutableLiveData<PhotoDomain?>()
    val navigateToDetailPhoto: LiveData<PhotoDomain?>
        get() = _navigateToDetailPhoto


    fun navigateToDetailPhoto(photoDomain: PhotoDomain){
        _navigateToDetailPhoto.value = photoDomain
    }

    fun doneNavigating(){
        _navigateToDetailPhoto.value = null
    }



}