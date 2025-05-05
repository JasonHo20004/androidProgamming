package com.example.imageloaderapp

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.loader.content.AsyncTaskLoader
import java.io.IOException

/**
 * AsyncTaskLoader implementation for loading images from a URL
 */
class ImageLoader(context: Context, private val imageUrl: String) : AsyncTaskLoader<Bitmap>(context) {

    private val TAG = "ImageLoader"
    private var cachedBitmap: Bitmap? = null

    override fun loadInBackground(): Bitmap? {
        if (imageUrl.isEmpty()) {
            return null
        }

        return try {
            // Use the utility method from MainActivity to download the image
            MainActivity.downloadImage(imageUrl)
        } catch (e: IOException) {
            Log.e(TAG, "Error loading image in AsyncTaskLoader: ${e.message}")
            null
        }
    }

    override fun onStartLoading() {
        // If we already have a result, deliver it immediately
        cachedBitmap?.let {
            deliverResult(it)
        } ?: forceLoad() // Otherwise, start loading
    }

    override fun deliverResult(data: Bitmap?) {
        // Cache the result
        cachedBitmap = data

        // If the loader is started, deliver the result
        if (isStarted) {
            super.deliverResult(data)
        }
    }

    override fun onStopLoading() {
        // Attempt to cancel the current load task if possible
        cancelLoad()
    }

    override fun onReset() {
        super.onReset()

        // Ensure the loader is stopped
        onStopLoading()

        // Clear the cached result
        cachedBitmap = null
    }

    override fun onCanceled(data: Bitmap?) {
        super.onCanceled(data)
        // Release resources if needed
    }
}