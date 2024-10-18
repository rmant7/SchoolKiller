package com.schoolkiller.presentation.common

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.gigamole.composescrollbars.Scrollbars
import com.gigamole.composescrollbars.config.ScrollbarsConfig
import com.gigamole.composescrollbars.config.ScrollbarsGravity
import com.gigamole.composescrollbars.config.ScrollbarsOrientation
import com.gigamole.composescrollbars.config.sizetype.ScrollbarsSizeType
import com.gigamole.composescrollbars.rememberScrollbarsState
import com.gigamole.composescrollbars.scrolltype.ScrollbarsScrollType
import com.gigamole.composescrollbars.scrolltype.knobtype.ScrollbarsStaticKnobType

@Composable
fun AppScrollBar(scrollState: ScrollState){ // Isn't draggable yet
    //val rawScrollState = rememberScrollState()

    val scrollbarsConfig = ScrollbarsConfig(
        orientation = ScrollbarsOrientation.Vertical,
        gravity = ScrollbarsGravity.End,
        sizeType = ScrollbarsSizeType.Full
    )

    val scrollbarsState = rememberScrollbarsState(
        config = scrollbarsConfig,
        scrollType = ScrollbarsScrollType.Scroll(
            ScrollbarsStaticKnobType.Exact(size = 40.dp),
            state = scrollState
        )
    )

    Scrollbars(state = scrollbarsState)
}