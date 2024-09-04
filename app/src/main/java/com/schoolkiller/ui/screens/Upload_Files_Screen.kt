package com.schoolkiller.ui.screens

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.schoolkiller.R
import com.schoolkiller.ui.reusable_components.ApplicationScaffold
import com.schoolkiller.ui.reusable_components.DropBox
import com.schoolkiller.ui.reusable_components.ScreenImage
import com.schoolkiller.ui.reusable_components.UniversalButton

@Composable
fun UploadFilesScreen(
    context: Context,
) {

    val uploadFileMethodOptions =
        remember { context.resources.getStringArray(R.array.upload_file_methods).toList() }
    var selectedUploadFileMethod by remember { mutableStateOf(uploadFileMethodOptions[0]) }


    ApplicationScaffold {

        ScreenImage(
            image = R.drawable.upload_to_school_assistant,
            contentDescription = R.string.upload_to_ai_school_image_assistant_content_description
        )

        DropBox(
            maxHeightIn = 200.dp,
            xDpOffset = 180.dp,
            yDpOffset = (-30).dp,
            label = R.string.upload_a_file_label,
            selectedOption = selectedUploadFileMethod,
            options = uploadFileMethodOptions,
            onOptionSelected = { selectedUploadFileMethod = it }
        )

        UniversalButton(label = R.string.solve_button_label) {
             // TODO { Implement Click Action }
        }

    }
}
