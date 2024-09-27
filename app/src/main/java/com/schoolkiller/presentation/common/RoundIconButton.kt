package com.schoolkiller.presentation.common

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
    icon: Int,
    onButtonClick: () -> Unit
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
            contentDescription = "Информация о приложении",
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape),
            tint = MaterialTheme.colorScheme.background,
        )
    }
}