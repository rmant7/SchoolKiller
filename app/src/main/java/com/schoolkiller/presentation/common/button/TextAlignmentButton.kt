package com.schoolkiller.presentation.common.button

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.schoolkiller.R

@Composable
fun TextAlignmentButton(
    layoutDirection: LayoutDirection,
    onUpdate: (LayoutDirection) -> Unit,
) {
    val isLtr = layoutDirection == LayoutDirection.Ltr
    val icon =
        if (isLtr) R.drawable.rtl
        else R.drawable.ltr

    RoundIconButton(
        icon = icon,
        iconModifier = Modifier.size(30.dp)
    ) {
        val newTextFieldLayoutDir =
            if (isLtr) LayoutDirection.Rtl
            else LayoutDirection.Ltr
        onUpdate(newTextFieldLayoutDir)
    }
}