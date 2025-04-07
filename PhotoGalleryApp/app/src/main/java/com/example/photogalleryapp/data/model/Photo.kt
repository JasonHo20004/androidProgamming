package com.example.photogalleryapp.data.model

import android.net.Uri
import java.util.UUID

data class Photo(
    val id: String = UUID.randomUUID().toString(),
    val uri: Uri,
    val title: String = "",
    val description: String = "",
    val dateTaken: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)