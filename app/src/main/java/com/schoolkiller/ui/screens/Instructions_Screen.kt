package com.schoolkiller.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.schoolkiller.R
import com.schoolkiller.ui.reusable_components.ApplicationScaffold
import com.schoolkiller.ui.reusable_components.DropBox
import com.schoolkiller.ui.reusable_components.ScreenImage
import com.schoolkiller.ui.reusable_components.UniversalButton

@Composable
fun InstructionsScreen(
    context: Context,
    modifier: Modifier = Modifier,
    onNavigateToNextPage: () -> Unit
) {

    val classOptions = remember { context.resources.getStringArray(R.array.grades).toList() }
    val languageOptions = remember { context.resources.getStringArray(R.array.languages).toList() }
    val explanationOptions = remember { context.resources.getStringArray(R.array.explanations).toList() }
    var selectedGrade by remember { mutableStateOf(classOptions[0]) }
    var selectedLanguage by remember { mutableStateOf(languageOptions[0]) }
    var selectedExplanation by remember { mutableStateOf(explanationOptions[0]) }
    var additionalInformationText by remember { mutableStateOf("") }


    ApplicationScaffold {

        ScreenImage(
            image = R.drawable.ai_school_assistant,
            contentDescription = R.string.ai_school_assistant_image_content_description
        )

        DropBox(
            dropMenuModifier = modifier
                .width(50.dp),
            maxHeightIn = 200.dp,
            xDpOffset = 265.dp,
            yDpOffset = (-30).dp,
            label = R.string.grade_label,
            selectedOption = selectedGrade,
            options = classOptions,
            onOptionSelected = { selectedGrade = it }
        )

        DropBox(
            maxHeightIn = 200.dp,
            xDpOffset = 155.dp,
            yDpOffset = (-30).dp,
            label = R.string.solution_language_label,
            selectedOption = selectedLanguage,
            options = languageOptions,
            onOptionSelected = { selectedLanguage = it }
        )

        DropBox(
            maxHeightIn = 200.dp,
            xDpOffset = 170.dp,
            yDpOffset = (-30).dp,
            label = R.string.explanations_label,
            selectedOption = selectedExplanation,
            options = explanationOptions,
            onOptionSelected = { selectedExplanation = it }
        )

        OutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(max = 95.dp),
            value = additionalInformationText,
            onValueChange = { additionalInformationText = it },
            label = {
                Text(
                    modifier = modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.additional_information_TextField_label),
                    textAlign = TextAlign.Start
                )
            }
        )

        UniversalButton(label = R.string.next_button_label) {
            onNavigateToNextPage()
        }

    }
}


