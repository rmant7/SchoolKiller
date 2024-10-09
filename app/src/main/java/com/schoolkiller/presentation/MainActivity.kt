package com.schoolkiller.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.google.ai.client.generativeai.type.content
import com.schoolkiller.presentation.common.ApplicationScaffold
import com.schoolkiller.presentation.navigation.NavigationController
import com.schoolkiller.presentation.ui.theme.SchoolKillerTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    //@OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SchoolKillerTheme {
                MaterialTheme(
                    shapes = shapes.copy(RoundedCornerShape(16.dp))
                ) {
                    // navController's back stack entry is always null
                    // needs fix

                    /*
                    val navController = rememberNavController()
                    ApplicationScaffold(
                        isShowed = true,
                        topBar = {
                            TopAppBar(
                                title = { Text(text = "") },
                                navigationIcon = {
                                    if (navController.previousBackStackEntry != null)
                                        IconButton(onClick = { navController.navigateUp() }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Back"
                                            )
                                        }
                                }

                            )

                        },
                        content = {
                            NavigationController(navController)
                        })
                    */
                    NavigationController()
                }
            }
        }
    }
}



