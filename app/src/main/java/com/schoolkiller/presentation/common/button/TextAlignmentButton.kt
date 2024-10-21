package com.schoolkiller.presentation.common.button

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.LayoutDirection
import com.schoolkiller.R

@Composable
fun TextAlignmentButton(
    layoutDirection: LayoutDirection,
    onUpdate: (LayoutDirection) -> Unit
) {
    RoundIconButton(
        icon = R.drawable.text_align_left
    ) {

        val newTextFieldLayoutDir =
            if (layoutDirection == LayoutDirection.Ltr)
                LayoutDirection.Rtl
            else
                LayoutDirection.Ltr
        onUpdate(newTextFieldLayoutDir)
    }
}