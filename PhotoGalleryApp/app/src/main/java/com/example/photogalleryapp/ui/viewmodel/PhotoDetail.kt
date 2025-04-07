package com.example.photogalleryapp.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photogalleryapp.data.model.Photo
import com.example.photogalleryapp.data.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoDetailViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val photoId: String = checkNotNull(savedStateHandle["photoId"])

    // Current photo
    val photo: StateFlow<Photo?> = photoRepository.getPhotoById(photoId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Get all photos to navigate between them
    private val allPhotos = photoRepository.getAllPhotos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Navigation indices
    val currentPhotoIndex: StateFlow<Int> = allPhotos.map { photos ->
        photos.indexOfFirst { it.id == photoId }.takeIf { it >= 0 } ?: 0
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val hasNext: StateFlow<Boolean> = allPhotos.map { photos ->
        val index = photos.indexOfFirst { it.id == photoId }
        index < photos.size - 1 && index >= 0
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val hasPrevious: StateFlow<Boolean> = allPhotos.map { photos ->
        val index = photos.indexOfFirst { it.id == photoId }
        index > 0
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    // Get next and previous photo IDs
    fun getNextPhotoId(): String? {
        val photos = allPhotos.value
        val currentIndex = photos.indexOfFirst { it.id == photoId }
        return if (currentIndex < photos.size - 1 && currentIndex >= 0) {
            photos[currentIndex + 1].id
        } else null
    }

    fun getPreviousPhotoId(): String? {
        val photos = allPhotos.value
        val currentIndex = photos.indexOfFirst { it.id == photoId }
        return if (currentIndex > 0) {
            photos[currentIndex - 1].id
        } else null
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            photo.value?.let {
                photoRepository.favoritePhoto(photoId, !it.isFavorite)
            }
        }
    }

    fun updatePhotoDetails(title: String, description: String) {
        viewModelScope.launch {
            photo.value?.let {
                val updatedPhoto = it.copy(title = title, description = description)
                photoRepository.updatePhoto(updatedPhoto)
            }
        }
    }
}