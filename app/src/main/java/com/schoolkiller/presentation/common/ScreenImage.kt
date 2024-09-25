package com.schoolkiller.presentation.common

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun ScreenImage(
    modifier: Modifier = Modifier,
    image: Int,
    contentDescription: Int
) {

    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation

    if (orientation != Configuration.ORIENTATION_LANDSCAPE) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                modifier = modifier
                    .height(250.dp)
                    .clip(RoundedCornerShape(16.dp)),
                painter = painterResource(image),
                contentDescription = stringResource(id = contentDescription),
                contentScale = ContentScale.Crop
            )
        }
    }
}