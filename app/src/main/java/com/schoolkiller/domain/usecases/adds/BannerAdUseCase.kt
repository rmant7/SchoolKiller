package com.schoolkiller.domain.usecases.adds

import android.content.Context
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.schoolkiller.presentation.view_model.SchoolKillerViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class BannerAdUseCase@Inject constructor(
    @ApplicationContext private val context: Context
) {

    private var adView: AdView? = null

    fun loadAd(
        adUnitId: String,
        viewModel: SchoolKillerViewModel,
        adSize: AdSize) = viewModel.viewModelScope.launch {
        adView = AdView(context).apply {
            this.adUnitId = adUnitId
            this.setAdSize(adSize)
            adListener = object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Timber.tag("BannerAd").d(adError.toString())
                }

                override fun onAdLoaded() {
                    Timber.tag("BannerAd").d("Ad was loaded.")
                }
            }
        }

        adView?.loadAd(AdRequest.Builder().build())
    }

    private fun getAdView(): AdView? = adView

    fun getBannerAd(): AdView? {
        return this.getAdView()
    }

}



