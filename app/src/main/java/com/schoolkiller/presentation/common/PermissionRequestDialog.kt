package com.schoolkiller.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.schoolkiller.R

@Composable
fun PermissionRequestDialog(
    modifier: Modifier = Modifier,
    permissionMessageRationale: PermissionMessageRationale,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onGoToAppSettings: () -> Unit,
) {

    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        content = {

            Surface(
                shape = RoundedCornerShape(16.dp),
                modifier = modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                ) {
                    Box(
                        modifier
                            .size(44.dp),
                        content = {
                            AsyncImage(
                                modifier = modifier,
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(R.drawable.attention) // Use the drawable resource ID directly
                                    .build(),
                                contentDescription = "icon",  // TODO { hardcoded string }
                                contentScale = ContentScale.Fit,
                            )
                        }
                    )

                    Text(
                        modifier = modifier
                            .padding(vertical = 16.dp)
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        text = stringResource(
                            permissionMessageRationale.getMessage(
                                isPermanentlyDeclined = isPermanentlyDeclined
                            )
                        ),
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        softWrap = true,
                        textAlign = TextAlign.Start
                    )

                    HorizontalDivider()

                    Row(
                        modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom,
                        content = {

                            Box(
                                modifier
                                    .fillMaxWidth()
                                    .weight(0.7f),
                                contentAlignment = Alignment.CenterStart,
                                content = {
                                    IconButton(
                                        modifier = modifier.fillMaxWidth(),
                                        content = {
                                            Text(
                                                modifier = modifier
                                                    .fillMaxWidth(),
                                                text = "Cancel",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 18.sp,
                                                softWrap = true,
                                                textAlign = TextAlign.Center
                                            )
                                        },
                                        onClick = {
                                            onDismiss()
                                        }
                                    )
                                }
                            )

                            Box(
                                modifier
                                    .fillMaxWidth()
                                    .weight(1.3f),
                                contentAlignment = Alignment.CenterEnd,
                                content = {
                                    IconButton(
                                        modifier = modifier.fillMaxWidth(),
                                        content = {
                                            Text(
                                                modifier = modifier
                                                    .fillMaxWidth(),
                                                text = if (isPermanentlyDeclined) {
                                                    "Grant permission"
                                                } else {
                                                    "OK"
                                                },
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 18.sp,
                                                softWrap = true,
                                                textAlign = TextAlign.Center
                                            )
                                        },
                                        onClick = {
                                            if (isPermanentlyDeclined) {
                                                onGoToAppSettings()
                                            } else {
                                                onConfirm()
                                            }
                                        }
                                    )
                                }
                            )

                        }
                    )
                }
            }

        }
    )


}

interface PermissionMessageRationale {
    fun getMessage(isPermanentlyDeclined: Boolean): Int


    class CameraPermissionMessage : PermissionMessageRationale {
        override fun getMessage(isPermanentlyDeclined: Boolean): Int {
            return if (!isPermanentlyDeclined) {
                R.string.camera_permission_rationale
            } else {
                R.string.permanent_denied_camera_permission_explanation
            }
        }
    }

    class ReadMediaPermissionMessage : PermissionMessageRationale {
        override fun getMessage(isPermanentlyDeclined: Boolean): Int {
            return if (!isPermanentlyDeclined) {
                R.string.read_media_permission_rationale
            } else {
                R.string.permanent_denied_read_media_permission_explanation
            }
        }
    }

    class ReadStoragePermissionMessage : PermissionMessageRationale {
        override fun getMessage(isPermanentlyDeclined: Boolean): Int {
            return if (!isPermanentlyDeclined) {
                R.string.read_storage_permission_rationale
            } else {
                R.string.permanent_denied_read_storage_permission_explanation
            }
        }
    }

    class WriteStoragePermissionMessage : PermissionMessageRationale {
        override fun getMessage(isPermanentlyDeclined: Boolean): Int {
            return if (!isPermanentlyDeclined) {
                R.string.write_storage_permission_rationale
            } else {
                R.string.permanent_denied_write_storage_permission_explanation
            }
        }
    }
}
