package com.schoolkiller.presentation.common.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun RoundIconButton(
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier.size(30.dp).clip(CircleShape),
    icon: Int,
    onButtonClick: () -> Unit,
) {
    IconButton(
        modifier = modifier
            .clip(CircleShape)
            .size(50.dp)
            .background(MaterialTheme.colorScheme.primary),
        onClick = { onButtonClick() },
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = "App description",  // TODO { hardcode string }
            modifier = iconModifier,
            tint = MaterialTheme.colorScheme.background,
        )
    }
}