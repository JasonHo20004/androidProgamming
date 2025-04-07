package com.example.photogalleryapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.photogalleryapp.ui.screens.GalleryScreen
import com.example.photogalleryapp.ui.screens.CameraScreen
import com.example.photogalleryapp.ui.screens.PhotoDetailScreen

@Composable
fun PhotoGalleryApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "gallery") {
        composable("gallery") {
            GalleryScreen(
                onPhotoClick = { photoId ->
                    navController.navigate("photo/$photoId")
                },
                onCameraClick = {
                    navController.navigate("camera")
                }
            )
        }
        composable(
            route = "photo/{photoId}",
            arguments = listOf(navArgument("photoId") { type = NavType.StringType })
        ) { backStackEntry ->
            PhotoDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPhoto = { photoId ->
                    navController.navigate("photo/$photoId") {
                        // Pop the current photo detail and navigate to new one
                        popUpTo("photo/{photoId}") { inclusive = true }
                    }
                }
            )
        }
        composable("camera") {
            CameraScreen(
                onPhotoCaptured = {
                    navController.popBackStack()
                }
            )
        }
    }
}