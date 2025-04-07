package com.example.photogalleryapp.data.repository

import com.example.photogalleryapp.data.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getAllPhotos(): Flow<List<Photo>>
    fun getFavoritePhotos(): Flow<List<Photo>>
    suspend fun addPhoto(photo: Photo)
    suspend fun updatePhoto(photo: Photo)
    suspend fun deletePhoto(photoId: String)
    suspend fun favoritePhoto(photoId: String, isFavorite: Boolean)
    fun getPhotoById(photoId: String): Flow<Photo?>
}