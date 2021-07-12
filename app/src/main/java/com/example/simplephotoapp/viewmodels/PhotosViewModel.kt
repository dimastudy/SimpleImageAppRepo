package com.example.simplephotoapp.viewmodels

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.simplephotoapp.data.models.Photo
import com.example.simplephotoapp.data.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import javax.inject.Inject

@HiltViewModel
class PhotosViewModel @Inject constructor(
    private val repository: PhotoRepository
) : ViewModel() {

    private val START_QUERY = "car"

    private val _currentQuery = MutableLiveData<String>()
    val currentQuery: LiveData<String>
        get() = _currentQuery

    private val _detailNavigation = MutableLiveData<Photo?>()
    val detailNavigation: LiveData<Photo?>
        get() = _detailNavigation

    private val _favoritePhotosNavigation = MutableLiveData<Boolean>()
    val favoritePhotosNavigation: LiveData<Boolean>
        get() = _favoritePhotosNavigation


    val photoData = currentQuery.switchMap { query ->
        repository.getPhoto(query).cachedIn(viewModelScope)
    }

    fun searchForPhotos(query: String) {
        _currentQuery.value = query
    }

    init {
        _currentQuery.value = START_QUERY
    }

    fun navigateToDetailScreen(photo: Photo) {
        _detailNavigation.value = photo
    }

    fun doneNavigatingToDetail() {
        _detailNavigation.value = null
    }

    fun navigateToFavoriteScreen() {
        _favoritePhotosNavigation.value = true
    }

    fun doneNavigatingToFavorite() {
        _favoritePhotosNavigation.value = false
    }


    fun clearFavoritesPhotos() {
        viewModelScope.launch {
            repository.clearDatabase()
        }
    }


}