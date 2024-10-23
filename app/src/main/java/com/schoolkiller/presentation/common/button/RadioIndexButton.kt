package com.schoolkiller.presentation.common.button

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

@Composable
fun RadioIndexButton(
    index: Int,
    currentIndex:Int,
    isEnabled: Boolean,
    onSelected: (Int) -> Unit
) {
    val isSelected = currentIndex == index
    val isClicked = remember { mutableStateOf(isSelected) }
    if (!isEnabled) isClicked.value = false // reset clicks

    fun onClick() {
        isClicked.value = true
        onSelected(index)
    }

    @Composable
    fun getUnselectedButtonColor(): Color {
        return if (isEnabled && !isClicked.value && !isSelected) Color.Yellow
        else MaterialTheme.colorScheme.secondary
    }

    RadioButton(
        selected = isSelected,
        onClick = { onClick() },
        enabled = isEnabled,
        colors = RadioButtonDefaults.colors(
            unselectedColor = getUnselectedButtonColor(),
        )
    )

}