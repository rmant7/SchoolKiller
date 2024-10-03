package com.schoolkiller.presentation.common

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable


@Composable
fun OpenGallery(
    pickMultipleMediaLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>,
) {

    try {
        pickMultipleMediaLauncher.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}











