package com.schoolkiller.presentation.common.button

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.schoolkiller.R

@Composable
fun TextAlignmentButton(
    iconModifier: Modifier = Modifier.size(30.dp).clip(CircleShape),
    layoutDirection: LayoutDirection,
    onUpdate: (LayoutDirection) -> Unit,
) {
    val isLtr = layoutDirection == LayoutDirection.Ltr
    val icon =
        if (isLtr) R.drawable.format_text_direction_rtl
        else R.drawable.format_text_direction_ltr

    RoundIconButton(
        icon = icon,
        iconModifier = iconModifier
    ) {
        val newTextFieldLayoutDir =
            if (isLtr) LayoutDirection.Rtl
            else LayoutDirection.Ltr
        onUpdate(newTextFieldLayoutDir)
    }
}