package com.schoolkiller.presentation.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.schoolkiller.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PictureItem(
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    context: Context,
    imageUri: Uri,
    onRemove: () -> Unit,
    onEnlarge: () -> Unit
) {

    var buttonColor by remember { mutableStateOf(Color.Black) }

    LaunchedEffect(imageUri) {
        val luminance = calculateImageLuminance(imageUri, context)
        buttonColor = if ( luminance < 0.5 ) Color.White else Color.Black
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
        ) {

            AsyncImage(
                modifier = modifier
                    .matchParentSize(),
                model = imageUri,
                contentDescription = "Picture",  // TODO { hardcoded string }
                error = painterResource(id = R.drawable.upload_to_school_assistant), // TODO { import an error image }
                placeholder = painterResource(id = R.drawable.ai_school_assistant), // TODO { import a placeholder image }
                //contentScale = ContentScale.FillBounds,
                contentScale = ContentScale.FillHeight
                )
            Row(
                modifier = imageModifier

                    .padding(vertical = 40.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                content = {

                    // Enlarge Button
                    IconButton(onClick = onEnlarge) {
                        Icon(
                            painter = painterResource(R.drawable.enlarge_image),
                            contentDescription = "Enlarge", // TODO { hardcoded string }
                            tint = buttonColor
                        )
                    }

                    Spacer(modifier.weight(1f))

                    // Remove Button
                    IconButton(onClick = onRemove) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove", // TODO { hardcoded string }
                            tint = buttonColor
                        )
                    }
                }
            )
        }
    }
}


@Composable
private fun formatDate(timestamp: Long): String {
    val formatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    return formatter.format(Date(timestamp))
}


private suspend fun calculateImageLuminance(imageUri: Uri, context: Context): Float {
    val request = ImageRequest.Builder(context)
        .data(imageUri)
        .build()

    val result = (ImageLoader(context).execute(request) as SuccessResult).drawable
    val bitmap = (result as BitmapDrawable).bitmap
    // Convert hardware bitmap to software bitmap
    val softwareBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false)

    return withContext(Dispatchers.Default) {
        softwareBitmap.calculateAverageLuminance()

    }
}

private fun Bitmap.calculateAverageLuminance(): Float {
    var totalLuminance = 0f
    for (x in 0 until width) {
        for (y in 0 until height) {
            val pixel = getPixel(x, y)
            val luminance = ColorUtils.calculateLuminance(pixel).toFloat()
            totalLuminance += luminance
        }
    }
    return totalLuminance / (width * height)
}