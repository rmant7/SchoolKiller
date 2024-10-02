package com.schoolkiller.presentation.common

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

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
                val bitmap = uriToBitmap(context, image)
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(top = 16.dp)
                        .background(Color.White),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
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
                    Button(
                        onDismiss,
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(0.dp, 10.dp)
                    ) { Text("Ok", fontSize = 20.sp) }
                }
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        )
    }
}



