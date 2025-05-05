package com.example.imageloaderapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Bitmap> {

    private val TAG = "MainActivity"
    private val IMAGE_LOADER_ID = 1

    private lateinit var urlEditText: EditText
    private lateinit var loadImageButton: Button
    private lateinit var statusTextView: TextView
    private lateinit var imageView: ImageView

    private lateinit var networkChangeReceiver: NetworkChangeReceiver
    private var useAsyncTaskLoader = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        urlEditText = findViewById(R.id.urlEditText)
        loadImageButton = findViewById(R.id.loadImageButton)
        statusTextView = findViewById(R.id.statusTextView)
        imageView = findViewById(R.id.imageView)

        // Set click listener for the load button
        loadImageButton.setOnClickListener {
            if (isNetworkAvailable()) {
                val imageUrl = urlEditText.text.toString().trim()
                if (imageUrl.isNotEmpty()) {
                    if (useAsyncTaskLoader) {
                        // Method 2: Using AsyncTaskLoader
                        startImageLoader(imageUrl)
                    } else {
                        // Method 1: Using AsyncTask
                        ImageDownloadTask().execute(imageUrl)
                    }
                } else {
                    Toast.makeText(this,
                        "Please enter a valid URL", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this,
                    "No internet connection available", Toast.LENGTH_SHORT).show()
            }
        }

        // Toggle between AsyncTask and AsyncTaskLoader
        findViewById<TextView>(R.id.titleTextView).setOnLongClickListener {
            useAsyncTaskLoader = !useAsyncTaskLoader
            Toast.makeText(this,
                "Using " + (if (useAsyncTaskLoader) "AsyncTaskLoader" else "AsyncTask"),
                Toast.LENGTH_SHORT).show()
            true
        }

        // Register network change receiver
        networkChangeReceiver = NetworkChangeReceiver()
        registerReceiver(networkChangeReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        // Start the background service
        startImageLoaderService()

        // Check initial network state
        updateNetworkStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the network change receiver
        unregisterReceiver(networkChangeReceiver)
    }

    /**
     * Method 1: AsyncTask Implementation for image loading
     */
    private inner class ImageDownloadTask : AsyncTask<String, Void, Bitmap?>() {
        private var errorMessage: String? = null

        override fun onPreExecute() {
            statusTextView.text = "Loading..."
            imageView.setImageResource(android.R.color.transparent)
        }

        override fun doInBackground(vararg urls: String): Bitmap? {
            return try {
                downloadImage(urls[0])
            } catch (e: IOException) {
                Log.e(TAG, "Error downloading image: " + e.message)
                errorMessage = "Failed to load image: " + e.message
                null
            }
        }

        override fun onPostExecute(bitmap: Bitmap?) {
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap)
                statusTextView.text = "Image loaded successfully"
            } else {
                statusTextView.text = errorMessage ?: "Unknown error"
            }
        }
    }

    /**
     * Method 2: AsyncTaskLoader Implementation
     */
    private fun startImageLoader(url: String) {
        statusTextView.text = "Loading with AsyncTaskLoader..."
        imageView.setImageResource(android.R.color.transparent)

        val args = Bundle().apply {
            putString("url", url)
        }

        // If a loader exists, restart it; otherwise initialize a new one
        val loaderManager = LoaderManager.getInstance(this)
        if (loaderManager.getLoader<Bitmap>(IMAGE_LOADER_ID) != null) {
            loaderManager.restartLoader(IMAGE_LOADER_ID, args, this)
        } else {
            loaderManager.initLoader(IMAGE_LOADER_ID, args, this)
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Bitmap> {
        return if (id == IMAGE_LOADER_ID && args != null) {
            val url = args.getString("url", "")
            ImageLoader(this, url)
        } else {
            ImageLoader(this, "")
        }
    }

    override fun onLoadFinished(loader: Loader<Bitmap>, data: Bitmap?) {
        if (data != null) {
            imageView.setImageBitmap(data)
            statusTextView.text = "Image loaded successfully with AsyncTaskLoader"
        } else {
            statusTextView.text = "Failed to load image with AsyncTaskLoader"
        }
    }

    override fun onLoaderReset(loader: Loader<Bitmap>) {
        // Clear the ImageView if needed
        imageView.setImageResource(android.R.color.transparent)
    }

    /**
     * Check if network is available
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    /**
     * Start the background service
     */
    private fun startImageLoaderService() {
        val serviceIntent = Intent(this, ImageLoaderService::class.java)
        startService(serviceIntent)
    }

    /**
     * Update UI based on network status
     */
    private fun updateNetworkStatus() {
        val isConnected = isNetworkAvailable()
        loadImageButton.isEnabled = isConnected

        statusTextView.text = if (!isConnected) {
            "No internet connection"
        } else {
            "Ready to load image"
        }
    }

    /**
     * BroadcastReceiver for network connectivity changes
     */
    inner class NetworkChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateNetworkStatus()
        }
    }

    companion object {
        /**
         * Utility method to download image from URL
         */
        @Throws(IOException::class)
        fun downloadImage(urlString: String): Bitmap? {
            var connection: HttpURLConnection? = null
            var input: InputStream? = null

            try {
                val url = URL(urlString)
                connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 15000
                connection.readTimeout = 10000
                connection.connect()

                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    throw IOException("HTTP error code: $responseCode")
                }

                input = connection.inputStream
                return BitmapFactory.decodeStream(input)
            } finally {
                input?.close()
                connection?.disconnect()
            }
        }
    }
}