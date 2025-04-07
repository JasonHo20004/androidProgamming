package com.example.photogalleryapp.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.photogalleryapp.ui.components.PhotoActionDialog
import com.example.photogalleryapp.ui.viewmodel.GalleryViewModel
import com.example.photogalleryapp.ui.viewmodel.PhotoDetailViewModel

/*
 * -----------------------------------------------------------------------------
 *  STEP 2 – show real error state instead of endless spinner
 *  STEP 3 – switch picker to OpenDocument + persistable permission
 *  STEP 4 – request READ_MEDIA_IMAGES / READ_EXTERNAL_STORAGE at runtime
 * -----------------------------------------------------------------------------
 */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel(),
    onPhotoClick: (String) -> Unit,
    onCameraClick: () -> Unit
) {
    val context = LocalContext.current
    val photos by viewModel.photos.collectAsState()
    var showPhotoActionDialog by remember { mutableStateOf(false) }
    var selectedPhotoId by remember { mutableStateOf("") }
    var showFabMenu by remember { mutableStateOf(false) }

    /* --------------------------------------------------------
     * Runtime permission launcher (Step 4)
     * -------------------------------------------------------- */
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            // you could show a snackbar here
            Log.w("GalleryScreen", "READ_MEDIA_IMAGES / READ_EXTERNAL_STORAGE not granted")
        }
    }

    LaunchedEffect(Unit) {
        val permission = if (Build.VERSION.SDK_INT >= 33) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(permission)
        }
    }

    /* --------------------------------------------------------
     * Image picker using OpenDocument (Step 3)
     * -------------------------------------------------------- */
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult

        // Persist read permission so Coil can reopen the URI later
        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        try {
            context.contentResolver.takePersistableUriPermission(uri, flags)
        } catch (t: Throwable) {
            Log.e("GalleryScreen", "takePersistableUriPermission failed for $uri", t)
        }
        viewModel.addPhoto(uri)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Photo Gallery") }) },
        floatingActionButton = {
            if (showFabMenu) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SmallFloatingActionButton(onClick = { pickImageLauncher.launch(arrayOf("image/*")) }) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = "Choose from gallery")
                    }
                    Spacer(Modifier.width(8.dp))
                    SmallFloatingActionButton(onClick = onCameraClick) {
                        Icon(Icons.Default.Camera, contentDescription = "Take photo")
                    }
                    Spacer(Modifier.width(8.dp))
                    FloatingActionButton(onClick = { showFabMenu = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close menu")
                    }
                }
            } else {
                FloatingActionButton(onClick = { showFabMenu = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Open menu")
                }
            }
        }
    ) { paddingValues ->
        Box(Modifier.fillMaxSize().padding(paddingValues)) {
            /* Debug counter – remove when happy */
            Text("Photo count = ${photos.size}", Modifier.align(Alignment.TopCenter))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 128.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(photos, key = { it.id }) { photo ->
                    Box(
                        Modifier
                            .size(128.dp)
                            .combinedClickable(
                                onClick = { onPhotoClick(photo.id) },
                                onLongClick = {
                                    selectedPhotoId = photo.id
                                    showPhotoActionDialog = true
                                }
                            )
                    ) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(photo.uri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Photo thumbnail",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            when (val s = painter.state) {
                                is AsyncImagePainter.State.Loading -> {
                                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                                }
                                is AsyncImagePainter.State.Error -> {
                                    Log.e("Coil‑Error", "Thumbnail load failed ${photo.uri}", s.result.throwable)
                                    Icon(Icons.Default.BrokenImage, null,
                                        tint = Color.Red, modifier = Modifier
                                            .size(32.dp)
                                            .align(Alignment.Center))
                                }
                                else -> Unit
                            }
                            LaunchedEffect(painter.state) {
                                if (painter.state is AsyncImagePainter.State.Success) {
                                    Log.d("Coil‑OK", "Rendered ${photo.uri}")
                                }
                            }

                        }


                        AnimatedVisibility(
                            visible = photo.isFavorite,
                            enter = fadeIn(tween(300)),
                            exit = fadeOut(tween(300)),
                            modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                        ) {
                            Badge(containerColor = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Favorite, contentDescription = "Favorite", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            if (showPhotoActionDialog) {
                PhotoActionDialog(
                    onDismiss = { showPhotoActionDialog = false },
                    onDelete = {
                        viewModel.deletePhoto(selectedPhotoId)
                        showPhotoActionDialog = false
                    },
                    onToggleFavorite = {
                        viewModel.toggleFavorite(selectedPhotoId)
                        showPhotoActionDialog = false
                    }
                )
            }
        }
    }
}