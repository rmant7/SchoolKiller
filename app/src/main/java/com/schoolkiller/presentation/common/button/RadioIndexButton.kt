package com.schoolkiller.presentation.common.button

import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
fun RadioIndexButton(
    index: Int,
    selectedIndex: MutableIntState,
    onClick: (Int) -> Unit,
    indexMax: () -> Int
) {

    val selectedOcrResultId = remember { mutableStateOf(selectedIndex) }

    fun changeTextVariant() {
        selectedOcrResultId.value.intValue = index
        onClick(index)
    }

    fun isSelected(): Boolean {
        return selectedOcrResultId.value.intValue == index
    }

    fun isEnabled(): Boolean {
        return indexMax.invoke() - 1 >= index
    }

    RadioButton(
        selected = isSelected(),
        onClick = { changeTextVariant() },
        enabled = isEnabled()
    )

}