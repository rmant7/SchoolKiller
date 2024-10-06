package com.schoolkiller.domain.model

import android.net.Uri
import com.google.android.gms.ads.AdView

data class ResultProperties(
    val mediumBannerAdview: AdView? = null,
    val requestGeminiResponse: Boolean = true,
    val isResultFetchedStatus: Boolean = false,
    val textGenerationResult: String = "",
    val passedImageUri: Uri? = null,
    val passedConvertedSolutionPrompt: String = "",
    val passedConvertedSolvePrompt: String = "",
    val isSolveActionRequested: Boolean = false,
    val error: Throwable? = null
)