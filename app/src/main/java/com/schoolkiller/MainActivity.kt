package com.schoolkiller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.ui.unit.dp
import com.google.android.gms.ads.MobileAds
import com.schoolkiller.ui.navigation.NavigationController
import com.schoolkiller.ui.theme.SchoolKillerTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    //@RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private lateinit var appOpenAdManager: AppOpenAdManager

    @RequiresApi(value = 34)
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
