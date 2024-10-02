package com.schoolkiller.domain.usecases.ads

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.schoolkiller.presentation.screens.home.HomeViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenAdUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) {


    fun loadOpenAd(adUnitId: String, viewModel: HomeViewModel) {
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
                        Timber.d(loadAdError.message)
                        viewModel.updateIsOpenAdLoading(false)
                    }
                }
            )
        }
    }
}