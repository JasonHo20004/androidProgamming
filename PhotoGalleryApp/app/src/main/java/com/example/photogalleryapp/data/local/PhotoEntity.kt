package com.example.photogalleryapp.data.local

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.photogalleryapp.data.model.Photo

@Entity(tableName = "photo_table")
@TypeConverters(Converters::class)
data class PhotoEntity(
    @PrimaryKey
    val id: String,
    val uri: Uri,
    val title: String,
    val description: String,
    val dateTaken: Long,
    val isFavorite: Boolean
)

// Extension functions to convert between domain and entity models
fun PhotoEntity.toPhoto(): Photo {
    return Photo(
        id = id,
        uri = uri,
        title = title,
        description = description,
        dateTaken = dateTaken,
        isFavorite = isFavorite
    )
}

fun Photo.toEntity(): PhotoEntity {
    return PhotoEntity(
        id = id,
        uri = uri,
        title = title,
        description = description,
        dateTaken = dateTaken,
        isFavorite = isFavorite
    )
}