package com.schoolkiller.ui.reusable_components

import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.schoolkiller.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@Composable
fun PictureItem(
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
    imageUri : Uri,
    offsetValue: Float,
    onOffsetChange: (Float) -> Unit,
    scope: CoroutineScope,
    state: LazyListState,
//    picture: Picture,
    onRemove: () -> Unit
) {
    var offset by remember { mutableFloatStateOf(offsetValue) }
    var isDragging by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
//            .pointerInput(Unit) {
//                detectTapGestures(
//
//                    onLongPress = {
//                        isDragging = true
//                    }
//                )
//            }
//            .draggable(
//                state = rememberDraggableState { delta ->
//                    offset += delta
//                },
//                orientation = Orientation.Vertical,
//                onDragStopped = { velocity ->
//                    scope.launch {
//                        // Calculate a target offset based on velocity
//                        val targetOffset = offset + (velocity * 0.5f)
//
//                        // Create an animation spec based on velocity
//                        val animationSpec = tween<Float>(
//                            durationMillis = (abs(velocity) * 0.5f).toInt(),
//                            easing = LinearEasing
//                        )
//
//                        // Smoothly scroll to the target offset
//                        state.animateScrollBy(targetOffset, animationSpec)
//
//                        onOffsetChange(0f)
//                    }
//                }
//            )
    ) {
        Row(
            modifier = imageModifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                modifier = modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                model = imageUri,
                contentDescription = "Picture",  // TODO { hardcoded string }
                error = painterResource(id = R.drawable.upload_to_school_assistant), // TODO { import an error image }
                placeholder = painterResource(id = R.drawable.ai_school_assistant) // TODO { import a placeholder image }
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Image Details
//            Column {
//                Text(text = imageUri.path ?: "")
//                Text(text = picture.pictureDescription ?: "")
//                Text(text = "Captured on: ${formatDate(picture.pictureTimestamp)}") // TODO { hardcoded string }
//            }

            Spacer(Modifier.weight(1f))

            // Remove Button
            IconButton(onClick = onRemove) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove") // TODO { hardcoded string }
            }
        }
    }
}

@Composable
private fun formatDate(timestamp: Long): String {
    val formatter = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    return formatter.format(Date(timestamp))
}