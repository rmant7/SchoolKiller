package com.schoolkiller.domain.usecases.adds

import javax.inject.Inject

class AdUseCases @Inject constructor(
    val openAdUseCase: OpenAdUseCase,
    val bannerAdUseCase: BannerAdUseCase,
    val interstitialAdUseCase: InterstitialAdUseCase
)
