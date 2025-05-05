package com.example.imageloaderapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

/**
 * Background service that periodically displays notifications
 */
class ImageLoaderService : Service() {

    private val TAG = "ImageLoaderService"
    private val handler = Handler()
    private lateinit var notificationRunnable: Runnable

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")

        // Create notification channel for Android Oreo and higher
        createNotificationChannel()

        // Create runnable for periodic notifications
        notificationRunnable = object : Runnable {
            override fun run() {
                showNotification()
                // Schedule the next notification
                handler.postDelayed(this, NOTIFICATION_INTERVAL)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")

        // Start as a foreground service with a notification
        startForeground(NOTIFICATION_ID, createNotification())

        // Schedule periodic notifications
        handler.postDelayed(notificationRunnable, NOTIFICATION_INTERVAL)

        // If service is killed, restart it
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        // Not used for this service
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")

        // Stop scheduling notifications
        handler.removeCallbacks(notificationRunnable)
    }

    /**
     * Create and display a notification
     */
    private fun showNotification() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, createNotification())
        Log.d(TAG, "Notification displayed")
    }

    /**
     * Create a notification object
     */
    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Image Loader Service")
        .setContentText("Image Loader Service is running")
        .setSmallIcon(android.R.drawable.ic_menu_gallery)
        .setContentIntent(createPendingIntent())
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    /**
     * Create PendingIntent for notification
     */
    private fun createPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE
    )

    /**
     * Create notification channel for Android Oreo and higher
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Image Loader Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for Image Loader Service notifications"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "ImageLoaderServiceChannel"
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_INTERVAL = 5 * 60 * 1000L // 5 minutes
    }
}