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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun AttentionAlertDialog(
    modifier: Modifier = Modifier,
    isShowed: Boolean,
    message: String,
    icon: Int? = null,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {

    if (isShowed) {
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
                                        .data(icon) // Use the drawable resource ID directly
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
                            text = message,
                            fontWeight = FontWeight.Normal,
                            fontSize = 18.sp,
                            softWrap = true,
                            textAlign = TextAlign.Start
                        )

                        Row(
                            modifier

                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Bottom,
                            content = {

                                Box(
                                    modifier
                                        .fillMaxWidth()
                                        .weight(1f),
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
                                                onCancel()
                                            }
                                        )
                                    }
                                )

                                Box(
                                    modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.CenterEnd,
                                    content = {
                                        IconButton(
                                            modifier = modifier.fillMaxWidth(),
                                            content = {
                                                Text(
                                                    modifier = modifier
                                                        .fillMaxWidth(),
                                                    text = "Ok",
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 18.sp,
                                                    softWrap = true,
                                                    textAlign = TextAlign.Center
                                                )
                                            },
                                            onClick = {
                                                onConfirm()
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
}