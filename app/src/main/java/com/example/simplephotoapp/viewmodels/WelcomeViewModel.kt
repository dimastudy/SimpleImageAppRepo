package com.example.simplephotoapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*


class WelcomeViewModel : ViewModel() {


    private val _navigateToPhotos = MutableLiveData<Boolean>()
    val navigateToPhotos: LiveData<Boolean>
        get() = _navigateToPhotos


    init {
        _navigateToPhotos.value = false
    }


    fun navigateToPhotosScreen() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { delay(3000) }
            _navigateToPhotos.value = true
        }

    }

    fun doneNavigating() {
        _navigateToPhotos.value = false
    }


}