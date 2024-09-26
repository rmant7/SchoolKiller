package com.schoolkiller.domain.usecases.adds

import android.app.Activity
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.schoolkiller.presentation.view_model.SchoolKillerViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

class OpenAdUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
//    viewModel: SchoolKillerViewModel,
) {

//    private var appOpenAd: AppOpenAd? = null
//    private var isLoading = false
//    private var loadTime: Long = 0
//    private val appOpenAd = viewModel.appOpenAd
//    private val isLoading = viewModel.isOpenAdLoading
//    private var loadTime = viewModel.openAdLoadTime


    fun loadOpenAd(adUnitId: String, viewModel: SchoolKillerViewModel,){
        if (!viewModel.isOpenAdLoading.value && viewModel.appOpenAd.value == null) {
            viewModel.updateIsOpenAdLoading(true)
            val adRequest = AdRequest.Builder().build()
            AppOpenAd.load(
                context,
                adUnitId,
                adRequest,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdLoaded(ad: AppOpenAd) {
                        viewModel.updateAppOpenAd(ad)
                        viewModel.updateIsOpenAdLoading(false)
                        viewModel.updateOpenAdLoadTime(System.currentTimeMillis())
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        viewModel.updateIsOpenAdLoading(false)
                        onAdFailedToLoad(loadAdError)
                    }
                }
            )
        }
    }
}