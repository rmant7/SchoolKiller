package com.schoolkiller.ui.reusable_components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.schoolkiller.R

@Composable
fun DropBox(
    modifier: Modifier = Modifier,
    dropMenuModifier: Modifier = Modifier,
    maxHeightIn: Dp? = null,
    xDpOffset: Dp? = null,
    yDpOffset: Dp? = null,
    label: Int,
    selectedOption: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    contentDescription: Int = R.string.ArrowDropDown_icon_content_description
) {
    var expanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
    ) {

        item {
            OutlinedTextField(
                modifier = modifier
                    .clickable { expanded = !expanded }
                    .fillMaxWidth(),
                value = selectedOption,
                readOnly = true,
                textStyle = TextStyle(textAlign = TextAlign.Start),
                onValueChange = { },
                label = {
                    Text(text = stringResource(id = label))
                },
                trailingIcon = {
                    Icon(
                        modifier = modifier.clickable { expanded = !expanded },
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = stringResource(id = contentDescription)
                    )
                }
            )
            DropdownMenu(
                modifier = dropMenuModifier
                    .width(IntrinsicSize.Min)
                    .heightIn(max = maxHeightIn ?: Dp.Infinity),
                offset = DpOffset(x = xDpOffset ?: 0.dp, y = yDpOffset ?: 0.dp),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->

                    DropdownMenuItem(
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        },
                        text = {
                            Text(
                                text = option,
                                textAlign = TextAlign.Start
                            )
                        }
                    )
                }
            }
        }
    }
}