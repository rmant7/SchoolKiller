package com.schoolkiller.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.ui.unit.dp
import com.schoolkiller.presentation.navigation.NavigationController
import com.schoolkiller.presentation.ui.theme.SchoolKillerTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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

    }

}
