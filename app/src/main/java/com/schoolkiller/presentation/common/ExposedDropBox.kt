import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ExposedDropBox(
    modifier: Modifier = Modifier,
    dropMenuModifier: Modifier = Modifier,
    maxHeightIn: Dp? = null,
    label: Int,
    selectedOption: (T),
    options: List<T>,
    onOptionSelected: (T) -> Unit,
    optionToString: (T, Context) -> String,
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            modifier = modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .clickable { expanded = !expanded }
                .fillMaxWidth(),
            value = optionToString(selectedOption, context),
            readOnly = true,
            textStyle = TextStyle(textAlign = TextAlign.Start),
            onValueChange = { },
            label = {
                Text(
                    text = stringResource(id = label),
                    textAlign = TextAlign.Start
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded,
                )
            }
        )
        ExposedDropdownMenu(
            modifier = dropMenuModifier
                .heightIn(max = maxHeightIn ?: Dp.Infinity),
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
                            text = optionToString(option, context),
                            textAlign = TextAlign.Start
                        )
                    }
                )
            }
        }
    }
}