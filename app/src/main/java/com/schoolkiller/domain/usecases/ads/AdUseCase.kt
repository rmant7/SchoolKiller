package com.schoolkiller.domain.usecases.ads

interface AdUseCase {
    fun shouldShowAdsCheck() {
        //if user has premium don't invoke
        //if (BuildConfig.is_advertisement_enabled.toBoolean()) return
    }
}