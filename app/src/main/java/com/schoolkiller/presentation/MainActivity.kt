package com.schoolkiller.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.schoolkiller.presentation.ads.BannerAdUseCase
import com.schoolkiller.presentation.ads.InterstitialAdUseCase
import com.schoolkiller.presentation.common.ApplicationScaffold
import com.schoolkiller.presentation.navigation.NavigationController
import com.schoolkiller.presentation.ui.theme.SchoolKillerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            SchoolKillerTheme {
                MaterialTheme(
                    shapes = shapes.copy(RoundedCornerShape(16.dp))
                ) {

                    navController = rememberNavController()
                    // observe back stack entries
                    navController.currentBackStackEntryAsState().value
                    ApplicationScaffold(
                        isShowed = true,
                        topBar = {
                            // No header for Home page
                            if (navController.previousBackStackEntry != null)
                                AppTopBar()
                        },
                        content = {
                            NavigationController(navController)
                        }
                    )

                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun AppTopBar() {

        TopAppBar(
            // modifier = Modifier.padding(0.dp, 25.dp),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.onSecondary
            ),
            title = { Text(text = "SchoolKiller") },
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )
    }

}