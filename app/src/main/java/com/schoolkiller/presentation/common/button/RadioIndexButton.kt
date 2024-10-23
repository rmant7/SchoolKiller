package com.schoolkiller.presentation.common.button

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

@Composable
fun RadioIndexButton(
    index: Int,
    currentIndex: Int,
    isEnabled: Boolean,
    onSelected: (Int) -> Unit
) {
    val isSelected = currentIndex == index
    val isClicked = remember { mutableStateOf(isSelected) }
    if (!isEnabled) isClicked.value = false // reset clicks

    val animatedColor = remember { Animatable(Color.White) }

    LaunchedEffect(isEnabled && !isClicked.value && !isSelected) {

        repeat(5) { // Number of flickers
            if (isSelected) {
                animatedColor.animateTo(Color.White)
                return@repeat // return from current loop if button is selected
            }
            animatedColor.animateTo(
                targetValue = Color.Yellow,
                animationSpec = tween(300) // Duration for each flicker
            )
            animatedColor.animateTo(
                targetValue = Color.White,
                animationSpec = tween(300)
            )
        }

    }


    fun onClick() {
        isClicked.value = true
        onSelected(index)
    }

    /*
    @Composable
    fun getUnselectedButtonColor(): Color {
        return if (isEnabled && !isClicked.value && !isSelected) Color.Yellow
        else MaterialTheme.colorScheme.secondary
    }
    */

    RadioButton(
        selected = isSelected,
        onClick = { onClick() },
        enabled = isEnabled,
        colors = RadioButtonDefaults.colors(
            unselectedColor = animatedColor.value,
        )
    )

}
