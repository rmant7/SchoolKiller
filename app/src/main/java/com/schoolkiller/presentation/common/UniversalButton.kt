package com.schoolkiller.presentation.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.ConfigurationCompat
import androidx.core.os.LocaleListCompat
import java.util.Locale

@Composable
fun UniversalButton(
    modifier: Modifier = Modifier,
    label: Int,
    onButtonClicked: () -> Unit
) {
    Button(
        modifier = modifier
            .padding(8.dp),
        onClick = {
            onButtonClicked()
        }
    ) {
        Text(
            modifier = modifier
                .wrapContentHeight()
                .align(Alignment.CenterVertically),
            text = stringResource(id = label),
            textAlign = TextAlign.Center,
            softWrap = true,
            fontSize = 18.sp
        )
    }
}


/** Track the system default language. Used to adjust buttons if needed with different languages */
@Composable
@ReadOnlyComposable
fun getSystemLocale(): Locale {
    val configuration = LocalConfiguration.current
    return ConfigurationCompat.getLocales(configuration).get(0)
        ?: LocaleListCompat.getDefault()[0]!!
}