package cz.pecawolf.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.automirrored.filled.ViewQuilt
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import cz.pecawolf.presentation.theme.PhotoAppTheme

@Composable
fun PaSwitchButton(
    textChecked: String,
    textUnchecked: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val content: @Composable (String) -> Unit = {
        Text(
            text = it,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
    PaSwitchButton(
        contentChecked = { content(textChecked) },
        contentUnchecked = { content(textUnchecked) },
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        modifier = modifier,
    )
}

@Composable
fun PaSwitchButton(
    iconChecked: Painter,
    iconUnchecked: Painter,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val content: @Composable (Painter) -> Unit = {
        Icon(
            painter = it,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
        )
    }
    PaSwitchButton(
        contentChecked = { content(iconChecked) },
        contentUnchecked = { content(iconUnchecked) },
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        modifier = modifier,
    )
}

@Composable
private fun PaSwitchButton(
    contentChecked: @Composable () -> Unit,
    contentUnchecked: @Composable () -> Unit,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .height(Dimensions.buttonMinSize)
            .wrapContentWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .clickable(enabled = enabled, onClick = { onCheckedChange(!checked) })
            .padding(all = Dimensions.spaceXSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .fillMaxHeight()
                .clip(MaterialTheme.shapes.small)
                .background(
                    color = if (checked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Transparent
                    },
                )
                .padding(horizontal = Dimensions.spaceXSmall),
            contentAlignment = Alignment.Center,
        ) {
            contentChecked()
        }
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .fillMaxHeight()
                .clip(MaterialTheme.shapes.small)
                .background(
                    color = if (!checked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        Color.Transparent
                    },
                )
                .padding(horizontal = Dimensions.spaceXSmall),
            contentAlignment = Alignment.Center,
        ) {
            contentUnchecked()
        }
    }
}

@Preview
@Composable
private fun PaSwitchButtonTextPreview() {
    PhotoAppTheme {
        PaSwitchButton(
            textChecked = "AM",
            textUnchecked = "PM",
            checked = true,
            onCheckedChange = {},
        )
    }
}

@Preview
@Composable
private fun PaSwitchButtonIconPreview() {
    PhotoAppTheme {
        PaSwitchButton(
            iconChecked = Icons.AutoMirrored.Default.ViewList.painter(),
            iconUnchecked = Icons.AutoMirrored.Default.ViewQuilt.painter(),
            checked = false,
            onCheckedChange = {},
        )
    }
}