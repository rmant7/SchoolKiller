package com.schoolkiller.presentation.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.schoolkiller.R
import io.ktor.client.plugins.ServerResponseException

@Composable
fun ErrorAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    throwable: Throwable,
    icon: ImageVector,
) {
    val dialogData = getAlertWindowData(throwable)

    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Icon")
        },
        title = {
            Text(text = stringResource(dialogData.first))
        },
        text = {
            Text(text = stringResource(dialogData.second))
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(R.string.Ok))
            }
        }
    )
}

private fun getAlertWindowData(t: Throwable?): Pair<Int, Int> {
    return when (t) {
        is ServerResponseException -> R.string.error_service_not_available_title to R.string.error_service_not_available
        else -> R.string.error_common_title to R.string.error_common_message
    }
}
