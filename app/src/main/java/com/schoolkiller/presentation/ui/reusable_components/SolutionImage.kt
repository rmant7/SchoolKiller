package com.schoolkiller.presentation.ui.reusable_components

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun SolutionImage(
    modifier: Modifier = Modifier,
    context: Context,
    image: Uri,
    contentDescription: String?
) {

    val configuration = LocalConfiguration.current
    val orientation = configuration.orientation

    if (orientation != Configuration.ORIENTATION_LANDSCAPE) {
        Box(
            modifier = modifier
                //.fillMaxWidth()
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            val bitmap = uriToBitmap(context, image)
            if (bitmap != null) {
                Image(
                    modifier = modifier
                        .size(400.dp),
                        //.height(250.dp),
                        //.clip(RoundedCornerShape(16.dp)),
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = contentDescription,
                    //contentScale = ContentScale.Crop
                )
            }
        }
    }
}

fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return if (Build.VERSION.SDK_INT < 28) {
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    } else {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    }
}


