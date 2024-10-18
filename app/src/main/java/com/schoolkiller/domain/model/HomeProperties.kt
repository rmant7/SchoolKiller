package com.schoolkiller.domain.model

import android.net.Uri
import com.google.android.gms.ads.appopen.AppOpenAd
import com.schoolkiller.domain.prompt.UploadFileMethodOptions

data class HomeProperties(
    val appOpenAdd : AppOpenAd? = null,
    val listOfImages: List<Uri> = emptyList(),
    val selectedUploadMethodOption: UploadFileMethodOptions = UploadFileMethodOptions.NO_OPTION,
    val selectedImageUri: Uri? = null,
    val isImageEnlarged: Boolean = false,
    )