package com.example.photogalleryapp.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun PhotoActionDialog(
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Photo Actions") },
        text = { Text("What would you like to do with this photo?") },
        confirmButton = {
            Button(onClick = onDelete) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onToggleFavorite) {
                Text("Toggle Favorite")
            }
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}