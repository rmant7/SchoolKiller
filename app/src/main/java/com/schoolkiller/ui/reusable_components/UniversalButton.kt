package com.schoolkiller.ui.reusable_components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun UniversalButton(
    modifier: Modifier = Modifier,
    label : Int,
    onButtonClicked : () -> Unit
){
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(vertical = 8.dp),
        onClick = {
            onButtonClicked()
        },
    ) {
        Text(stringResource(id = label))
    }
}