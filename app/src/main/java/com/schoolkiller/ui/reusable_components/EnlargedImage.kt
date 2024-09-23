package com.schoolkiller.ui.reusable_components

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun EnlargedImage(
    modifier: Modifier = Modifier,
    context: Context,
    isImageEnlarged: Boolean,
    image: Uri,
    onDismiss: () -> Unit,

    ) {
    if (isImageEnlarged) {
        Dialog(
            onDismissRequest = { onDismiss() },
            content = {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    val bitmap = uriToBitmap(context, image)
                    if (bitmap != null) {
                        Image(
                            modifier = modifier
                                .fillMaxHeight(0.7f)
                                .fillMaxWidth(0.9f)
                                .clip(RoundedCornerShape(16.dp)),
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }
            }
        )
    }

}