package com.example.photogalleryapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.photogalleryapp.ui.components.CameraCapture
import com.example.photogalleryapp.ui.components.CameraPermissionHandler
import com.example.photogalleryapp.ui.viewmodel.GalleryViewModel

@Composable
fun CameraScreen(
    viewModel: GalleryViewModel = hiltViewModel(),
    onPhotoCaptured: () -> Unit
) {
    CameraPermissionHandler(
        onPermissionGranted = {
            CameraCapture(
                onImageCaptured = { uri ->
                    viewModel.addPhoto(uri)
                    onPhotoCaptured()
                },
                onClose = onPhotoCaptured
            )
        }
    )
}