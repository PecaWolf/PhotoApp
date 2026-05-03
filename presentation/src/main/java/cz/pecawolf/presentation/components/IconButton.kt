package cz.pecawolf.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import cz.pecawolf.presentation.R
import cz.pecawolf.presentation.theme.PhotoAppTheme

@Composable
fun PaIconButtonPrimary(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.onSecondary,
    backgroundColor: Color = MaterialTheme.colorScheme.secondary,
    onClick: () -> Unit,
) {
    PaIconButtonBase(
        modifier = modifier,
        onClick = onClick,
        painter = painter,
        enabled = enabled,
        enabledColor = color,
        disabledColor = color,
        enabledBackgroundColor = backgroundColor,
        disabledBackgroundColor = MaterialTheme.colorScheme.onSecondary
            .copy(alpha = backgroundColor.alpha),
        enabledBorderColor = if (backgroundColor.alpha == 1f) {
            backgroundColor
        } else {
            Color.Transparent
        },
        disabledBorderColor = if (backgroundColor.alpha == 1f) {
            MaterialTheme.colorScheme.onSecondary
        } else {
            Color.Transparent
        },
        contentDescription = contentDescription,
    )
}

@Composable
fun PaIconButtonSecondary(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.secondary,
    backgroundColor: Color = Color.Transparent,
    onClick: () -> Unit,
) {
    PaIconButtonBase(
        modifier = modifier,
        onClick = onClick,
        painter = painter,
        enabled = enabled,
        enabledColor = color,
        disabledColor = MaterialTheme.colorScheme.onSecondary,
        enabledBackgroundColor = backgroundColor,
        enabledBorderColor = color,
        disabledBorderColor = MaterialTheme.colorScheme.onSecondary,
        contentDescription = contentDescription,
    )
}

@Composable
fun PaIconButtonTertiary(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = Color.Transparent,
    color: Color = MaterialTheme.colorScheme.secondary,
    onClick: () -> Unit,
) {
    PaIconButtonBase(
        modifier = modifier,
        onClick = onClick,
        painter = painter,
        enabled = enabled,
        enabledColor = color,
        disabledColor = MaterialTheme.colorScheme.onSecondary,
        enabledBackgroundColor = backgroundColor,
        disabledBackgroundColor = Color.Transparent,
        enabledBorderColor = Color.Transparent,
        disabledBorderColor = Color.Transparent,
        contentDescription = contentDescription,
    )
}

@Composable
fun PaBackIconButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit,
) {
    PaTopBarIconButton(
        modifier = modifier,
        onClick = onClick,
        painter = Icons.AutoMirrored.Default.ArrowBack.painter(),
        color = color,
        backgroundColor = backgroundColor,
        contentDescription = R.string.general_back_button.string(),
    )
}

@Composable
fun PaCloseIconButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit,
) {
    PaTopBarIconButton(
        modifier = modifier,
        onClick = onClick,
        painter = Icons.Default.Close.painter(),
        color = color,
        backgroundColor = backgroundColor,
        contentDescription = R.string.general_close.string(),
    )
}

@Composable
fun PaTopBarIconButton(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit,
) {
    PaIconButtonTertiary(
        modifier = modifier,
        onClick = onClick,
        painter = painter,
        color = color,
        backgroundColor = backgroundColor,
        contentDescription = contentDescription,
    )
}

@Composable
private fun PaIconButtonBase(
    onClick: () -> Unit,
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    enabledColor: Color = MaterialTheme.colorScheme.secondary,
    enabledBackgroundColor: Color = Color.Transparent,
    enabledBorderColor: Color = Color.Transparent,
    disabledColor: Color = MaterialTheme.colorScheme.secondary,
    disabledBackgroundColor: Color = Color.Transparent,
    disabledBorderColor: Color = Color.Transparent,
    shape: Shape = CircleShape,
) {
    val (iconColor, backgroundColor, borderColor) = if (enabled) {
        Triple(
            enabledColor,
            enabledBackgroundColor,
            enabledBorderColor,
        )
    } else {
        Triple(
            disabledColor,
            disabledBackgroundColor,
            disabledBorderColor,
        )
    }
    Box(
        modifier = modifier
            .size(Dimensions.buttonMinSize)
            .clip(shape)
            .clickable(
                enabled = enabled,
                onClick = onClick,
            )
            .background(backgroundColor)
            .border(
                color = borderColor,
                width = Dimensions.buttonBorderWidth,
                shape = shape,
            )
            .padding(Dimensions.spaceSmall),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            tint = iconColor,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true)
@Composable
private fun PaIconButtonPreview() {
    PhotoAppTheme {
        FlowRow(
            modifier = Modifier.padding(Dimensions.spaceSmall),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spaceSmall),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spaceSmall),
            maxItemsInEachRow = 3,
        ) {
            PaIconButtonPrimary(
                onClick = {},
                painter = Icons.Default.Add.painter(),
                contentDescription = "Add",
                enabled = true,
            )
            PaIconButtonSecondary(
                onClick = {},
                painter = Icons.Default.Add.painter(),
                contentDescription = "Add",
                enabled = true,
            )
            PaIconButtonTertiary(
                onClick = {},
                painter = Icons.Default.Add.painter(),
                contentDescription = "Add",
                enabled = true,
            )
            PaIconButtonPrimary(
                onClick = {},
                painter = Icons.Default.Add.painter(),
                contentDescription = "Add",
                enabled = false,
            )
            PaIconButtonSecondary(
                onClick = {},
                painter = Icons.Default.Add.painter(),
                contentDescription = "Add",
                enabled = false,
            )
            PaIconButtonTertiary(
                onClick = {},
                painter = Icons.Default.Add.painter(),
                contentDescription = "Add",
                enabled = false,
            )
        }
    }
}
