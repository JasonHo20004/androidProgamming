package com.example.photogalleryapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photogalleryapp.data.model.Photo
import com.example.photogalleryapp.data.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val photoRepository: PhotoRepository
) : ViewModel() {

    // UI states
    private val _isGridView = MutableStateFlow(true)
    val isGridView: StateFlow<Boolean> = _isGridView.asStateFlow()

    // Photos state from repository
    val photos = photoRepository.getAllPhotos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoritePhotos = photoRepository.getFavoritePhotos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Handlers for user actions
    fun addPhoto(uri: Uri, title: String = "", description: String = "") {
        viewModelScope.launch {
            val newPhoto = Photo(
                uri = uri,
                title = title,
                description = description
            )
            photoRepository.addPhoto(newPhoto)
        }
    }

    fun toggleFavorite(photoId: String) {
        viewModelScope.launch {
            val photo = photos.value.find { it.id == photoId }
            photo?.let {
                photoRepository.favoritePhoto(photoId, !it.isFavorite)
            }
        }
    }

    fun deletePhoto(photoId: String) {
        viewModelScope.launch {
            photoRepository.deletePhoto(photoId)
        }
    }

    fun toggleViewMode() {
        _isGridView.value = !_isGridView.value
    }
}