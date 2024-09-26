package com.schoolkiller.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.MobileAds
import com.schoolkiller.domain.usecases.adds.AppOpenAdManager
import com.schoolkiller.presentation.ui.navigation.NavigationController
import com.schoolkiller.presentation.ui.theme.SchoolKillerTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var appOpenAdManager: AppOpenAdManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SchoolKillerTheme {
                MaterialTheme(
                    shapes = shapes.copy(RoundedCornerShape(16.dp))
                ) {
                    NavigationController()
                }
            }
        }

        MobileAds.initialize(this) {
            Timber.tag("tag").d("Initializing MobileAds")
        }
        appOpenAdManager = AppOpenAdManager()
    }

    override fun onStart() {
        super.onStart()
        appOpenAdManager.fetchAd(this)
    }

}
