package com.schoolkiller.presentation.ui.reusable_components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UniversalButton(
    modifier: Modifier = Modifier,
    label : Int,
    onButtonClicked : () -> Unit
){
    Button(
        modifier = modifier
            .wrapContentHeight()
            //.height(70.dp)
            .padding(vertical = 8.dp),
        onClick = {
            onButtonClicked()
        }
    ) {
        Text(
            modifier = modifier
                .fillMaxWidth(),
            text = stringResource(id = label),
            textAlign = TextAlign.Center,
            softWrap = true,
            fontSize = 18.sp
            )
    }
}