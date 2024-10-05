package com.schoolkiller.domain.model

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.android.gms.ads.appopen.AppOpenAd
import com.schoolkiller.domain.UploadFileMethodOptions

data class HomeProperties(
    val appOpenAdd : AppOpenAd? = null,
    val listOfImages: List<Uri> = emptyList(),
    val selectedUploadMethodOption: UploadFileMethodOptions = UploadFileMethodOptions.NO_OPTION,
    val selectedImageUri: Uri? = null,
    val selectedImageIndex : Int? = null,
    val isImageEnlarged: Boolean = false,


    )