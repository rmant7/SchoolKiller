package com.schoolkiller.presentation.ui.ads

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdView
import com.schoolkiller.presentation.screens.checking.SolutionCheckingViewModel

@Composable
fun BannerAdContainer(
    modifier: Modifier = Modifier,
    viewModel: SolutionCheckingViewModel,
    adView: AdView?

) {
    if (adView == null) {
        viewModel.loadBannerAd()
    } else {
        AndroidView(
            factory = { adView }, // Assuming adView is successfully loaded
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight() // Adjust modifier as needed
        ) { view ->
            (view as AdView).resume() // Resume ad when it becomes visible
        }
    }
}