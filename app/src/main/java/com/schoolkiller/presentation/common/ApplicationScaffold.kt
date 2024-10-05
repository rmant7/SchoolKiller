package com.schoolkiller.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ApplicationScaffold(
    modifier: Modifier = Modifier,
    isShowed: Boolean,
    columnModifier: Modifier = Modifier,
    columnVerticalArrangement: Arrangement. Vertical = Arrangement.spacedBy(16.dp),
    columnHorizontalAlignment: Alignment. Horizontal = Alignment.Start,
    content: @Composable () -> Unit,
    bottomBar : @Composable () -> Unit = {},
) {
if (isShowed){
    Scaffold(
        modifier = modifier,
        bottomBar = bottomBar,
        content = { paddingValues ->
            Column(
                modifier = columnModifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues)
                    .padding(16.dp),
                verticalArrangement = columnVerticalArrangement,
                horizontalAlignment = columnHorizontalAlignment
            ) {
                content()
            }
        }
    )
}
}