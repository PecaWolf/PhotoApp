package cz.pecawolf.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PaBottomSheet(
    isVisible: Boolean,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = Dimensions.spaceMedium,
    content: @Composable ColumnScope.() -> Unit,
) {
    LaunchedEffect(isVisible) {
        if (isVisible) {
            sheetState.show()
        } else {
            sheetState.hide()
            onDismissRequest()
        }
    }

    if (!sheetState.isVisible && !isVisible) {
        return
    }

    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        content = {
            content()
            Spacer(modifier = Modifier.height(bottomPadding))
        },
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun <T> PaBottomSheet(
    target: T?,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = Dimensions.spaceMedium,
    content: @Composable ColumnScope.(T) -> Unit,
) {
    val isVisible: Boolean = remember(target != null) { target != null }

    PaBottomSheet(
        isVisible = isVisible,
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        bottomPadding = bottomPadding,
        content = { target?.let { content(it) } },
    )
}