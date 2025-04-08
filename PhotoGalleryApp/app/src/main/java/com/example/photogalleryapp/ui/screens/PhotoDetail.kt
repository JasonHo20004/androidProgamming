package com.example.photogalleryapp.ui.screens

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.photogalleryapp.ui.viewmodel.PhotoDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PhotoDetailScreen(
    viewModel: PhotoDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToPhoto: (String) -> Unit
) {
    val context = LocalContext.current
    val photo by viewModel.photo.collectAsState()
    val hasNext by viewModel.hasNext.collectAsState()
    val hasPrevious by viewModel.hasPrevious.collectAsState()

    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    val animatedScale by animateFloatAsState(scale, label = "scale")

    LaunchedEffect(photo?.id) { scale = 1f; offsetX = 0f; offsetY = 0f }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(photo?.title ?: "Photo Detail") },
            navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } },
            actions = { IconButton(onClick = { viewModel.toggleFavorite() }) { Icon(if (photo?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null) } }
        )
    }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            photo?.let { current ->
                Box(Modifier.fillMaxSize().pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(1f, 3f)
                        if (scale > 1f) { offsetX += pan.x; offsetY += pan.y }
                    }
                }) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context).data(current.uri).crossfade(true).build(),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize().graphicsLayer {
                            scaleX = animatedScale; scaleY = animatedScale; translationX = offsetX; translationY = offsetY
                        }
                    ) {
                        when (painter.state) {
                            is AsyncImagePainter.State.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                            is AsyncImagePainter.State.Error -> Icon(Icons.Default.BrokenImage, null, tint = Color.Red, modifier = Modifier.align(Alignment.Center))
                            else -> SubcomposeAsyncImageContent()
                        }
                    }
                }
                Row(Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = { viewModel.getPreviousPhotoId()?.let(onNavigateToPhoto) }, enabled = hasPrevious) { Text("Previous") }
                    Button(onClick = { viewModel.getNextPhotoId()?.let(onNavigateToPhoto) }, enabled = hasNext) { Text("Next") }
                }
            } ?: CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    }
}