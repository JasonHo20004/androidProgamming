package com.example.imageloaderapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log

/**
 * BroadcastReceiver for monitoring network connectivity changes
 * This standalone class allows for network monitoring even when the app isn't active
 */
class NetworkChangeReceiver : BroadcastReceiver() {

    private val TAG = "NetworkChangeReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "Network connectivity changed")

        // Only proceed if this is a connectivity change event
        if (ConnectivityManager.CONNECTIVITY_ACTION == intent.action) {
            val isConnected = isNetworkAvailable(context)

            // Broadcast the network state change to any interested components
            val localIntent = Intent("com.example.imageloaderapp.NETWORK_CHANGE").apply {
                putExtra("isConnected", isConnected)
            }
            context.sendBroadcast(localIntent)

            Log.d(TAG, "Network is ${if (isConnected) "connected" else "disconnected"}")
        }
    }

    /**
     * Check network availability
     */
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}