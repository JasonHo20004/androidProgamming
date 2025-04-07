package com.example.photogalleryapp.data.repository

import android.net.Uri
import android.util.Log
import com.example.photogalleryapp.data.local.PhotoDao
import com.example.photogalleryapp.data.local.toEntity
import com.example.photogalleryapp.data.local.toPhoto
import com.example.photogalleryapp.data.model.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepositoryImpl @Inject constructor(
    private val photoDao: PhotoDao

) : PhotoRepository {

    override fun getAllPhotos(): Flow<List<Photo>> =
        photoDao.getAllPhotos().map { entities -> entities.map { it.toPhoto() } }

    override fun getFavoritePhotos(): Flow<List<Photo>> =
        photoDao.getFavoritePhotos().map { entities -> entities.map { it.toPhoto() } }

    override suspend fun addPhoto(photo: Photo) {
        Log.d("Repo", "Inserting ${photo.uri}")
        photoDao.insertPhoto(photo.toEntity())
    }

    override suspend fun updatePhoto(photo: Photo) {
        photoDao.updatePhoto(photo.toEntity())
    }

    override suspend fun deletePhoto(photoId: String) {
        photoDao.deletePhotoById(photoId)
    }

    override suspend fun favoritePhoto(photoId: String, isFavorite: Boolean) {
        val photoFlow = photoDao.getPhotoById(photoId)
        photoFlow.collect { entity ->
            entity?.let {
                val updated = it.copy(isFavorite = isFavorite)
                photoDao.updatePhoto(updated)
            }
            // Only collect once to update the item
            return@collect
        }
    }

    override fun getPhotoById(photoId: String): Flow<Photo?> =
        photoDao.getPhotoById(photoId).map { entity -> entity?.toPhoto() }
}