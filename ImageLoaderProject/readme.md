# Image Loader App - 22110020_Ho Thanh Dat

This Android application loads images from user-provided URLs and demonstrates various Android concepts including AsyncTask, AsyncTaskLoader, broadcast receivers, and services with notifications.

## Features

- Load images from a URL using both AsyncTask and AsyncTaskLoader
- Monitor network connectivity changes using BroadcastReceiver
- Display periodic notifications using a background service
- Handle configuration changes (e.g., screen rotation) while preserving loaded images
- Provide user feedback during loading and error states

## Implementation Details

### 1. AsyncTask Implementation

The app initially uses `AsyncTask` to load images in the background:

- `ImageDownloadTask` class extends `AsyncTask<String, Void, Bitmap>`
- `onPreExecute()`: Updates UI to show loading state
- `doInBackground()`: Downloads the image from the provided URL
- `onPostExecute()`: Updates UI with the downloaded image or error message

### 2. AsyncTaskLoader Implementation

The app also implements `AsyncTaskLoader` as a more robust solution:

- `ImageLoader` class extends `AsyncTaskLoader<Bitmap>`
- Handles configuration changes properly by caching results
- Preserves the loaded image across activity recreation
- Uses `LoaderManager` to manage loader lifecycle

You can switch between AsyncTask and AsyncTaskLoader by long-pressing the app title.

### 3. Network Connectivity Monitoring

The app monitors network connectivity changes:

- `NetworkChangeReceiver` extends `BroadcastReceiver` to listen for `CONNECTIVITY_ACTION`
- The app checks for an active internet connection before loading images
- UI feedback is provided when no connection is available
- The load button is disabled when offline

### 4. Background Service with Notifications

The app includes a foreground service that:

- Runs in the background using `ImageLoaderService`
- Displays a notification every 5 minutes
- Uses `NotificationCompat` to ensure compatibility across Android versions
- Creates a notification channel for Android Oreo and higher
- Opens the app when the notification is clicked

## Setup Instructions

1. Clone this repository
2. Open the project in Android Studio
3. Build and run the app on an emulator or physical device

## Permissions

The app requires the following permissions:

- `INTERNET`: To download images from URLs
- `ACCESS_NETWORK_STATE`: To monitor network connectivity
- `FOREGROUND_SERVICE`: To run the service in the foreground
- `POST_NOTIFICATIONS`: To display notifications

## Usage

1. Enter a valid image URL in the input field
2. Click the "Load Image" button to fetch and display the image
3. Long-press the app title to toggle between AsyncTask and AsyncTaskLoader
4. The app will show notifications periodically while running

## Testing

- Test the app with various image URLs
- Test network changes by toggling Wi-Fi or mobile data
- Test configuration changes by rotating the device
- Verify that the service continues to show notifications even when the app is in the background

## Best Practices Implemented

- Proper handling of activity lifecycle
- Efficient resource management
- Error handling for network operations
- User feedback during asynchronous operations
- Compatibility with different Android versions