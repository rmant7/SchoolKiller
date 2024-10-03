package com.schoolkiller.presentation.ads

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdView

@Composable
fun BannerAdContainer(
    modifier: Modifier = Modifier,
    adView : AdView?,
) {

    if (adView != null) {
        AndroidView(
            factory = { adView },
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
        ) { view ->
            (view as AdView).resume()
        }
    }
}