package cz.pecawolf.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import cz.pecawolf.presentation.theme.PhotoAppTheme

@Composable
fun PaButtonPrimary(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    shape: Shape? = null,
    color: Color? = null,
) {
    val finalColor = color ?: MaterialTheme.colorScheme.primary
    PaButtonBase(
        modifier = modifier,
        text = text,
        onClick = onClick,
        enabledTextColor = MaterialTheme.colorScheme.onPrimary,
        disabledTextColor = MaterialTheme.colorScheme.onSecondary,
        enabledBackgroundColor = finalColor,
        disabledBackgroundColor = MaterialTheme.colorScheme.secondary,
        enabledBorderColor = finalColor,
        disabledBorderColor = MaterialTheme.colorScheme.secondary,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        shape = shape,
        loading = loading,
    )
}

@Composable
fun PaButtonSecondary(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    shape: Shape? = null,
    color: Color? = null,
) {
    val finalColor = color ?: MaterialTheme.colorScheme.primary
    PaButtonBase(
        modifier = modifier,
        text = text,
        onClick = onClick,
        enabledTextColor = finalColor,
        disabledTextColor = MaterialTheme.colorScheme.secondary,
        enabledBackgroundColor = Color.Transparent,
        disabledBackgroundColor = Color.Transparent,
        enabledBorderColor = finalColor,
        disabledBorderColor = MaterialTheme.colorScheme.secondary,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        shape = shape,
        loading = loading,
    )
}

@Composable
fun PaButtonTertiary(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    shape: Shape? = null,
    color: Color? = null,
) {
    val finalColor = color ?: MaterialTheme.colorScheme.primary
    PaButtonBase(
        modifier = modifier,
        text = text,
        onClick = onClick,
        enabledTextColor = finalColor,
        disabledTextColor = MaterialTheme.colorScheme.secondary,
        enabledBackgroundColor = Color.Transparent,
        disabledBackgroundColor = Color.Transparent,
        enabledBorderColor = Color.Transparent,
        disabledBorderColor = Color.Transparent,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        shape = shape,
        loading = loading,
    )
}

@Composable
private fun PaButtonBase(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabledTextColor: Color,
    disabledTextColor: Color,
    enabledBackgroundColor: Color,
    disabledBackgroundColor: Color,
    enabledBorderColor: Color,
    disabledBorderColor: Color,
    leadingIcon: Painter?,
    trailingIcon: Painter?,
    shape: Shape? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    val (textColor, backgroundColor, borderColor) = if (enabled) {
        Triple(
            enabledTextColor,
            enabledBackgroundColor,
            enabledBorderColor,
        )
    } else {
        Triple(
            disabledTextColor,
            disabledBackgroundColor,
            disabledBorderColor,
        )
    }
    val finalShape = shape ?: MaterialTheme.shapes.extraLarge

    Row(
        modifier = modifier
            .heightIn(min = Dimensions.buttonMinSize)
            .widthIn(min = Dimensions.buttonMinSize)
            .clip(finalShape)
            .clickable(
                onClick = onClick,
                enabled = enabled && !loading,
            )
            .background(
                color = backgroundColor,
                shape = finalShape,
            )
            .border(
                color = borderColor,
                width = Dimensions.buttonBorderWidth,
                shape = finalShape,
            )
            .padding(horizontal = Dimensions.spaceSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            space = Dimensions.spaceSmall,
            alignment = Alignment.CenterHorizontally,
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(Dimensions.buttonMinSize)
                    .padding(all = Dimensions.spaceXSmall),
                color = textColor,
            )
        } else {
            leadingIcon?.let {
                Icon(
                    modifier = Modifier.size(Dimensions.iconSizeSmall),
                    painter = it,
                    contentDescription = null,
                    tint = textColor,
                )
            }
            AnimatedContent(
                modifier = Modifier.padding(horizontal = Dimensions.spaceXSmall),
                targetState = text,
            ) {
                Text(
                    text = it,
                    color = textColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            trailingIcon?.let {
                Icon(
                    modifier = Modifier.size(Dimensions.iconSizeSmall),
                    painter = it,
                    contentDescription = null,
                    tint = textColor,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PaButtonPreview() {
    PhotoAppTheme {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(Dimensions.spaceSmall),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.spaceSmall),
        ) {
            item {
                PaButtonPrimary(
                    text = "Primary",
                    onClick = {},
                    leadingIcon = Icons.Default.Favorite.painter(),
                    trailingIcon = Icons.Default.FavoriteBorder.painter(),
                )
            }
            item {
                PaButtonPrimary(
                    text = "Primary",
                    onClick = {},
                    leadingIcon = Icons.Default.Favorite.painter(),
                    trailingIcon = Icons.Default.FavoriteBorder.painter(),
                    enabled = false,
                )
            }
            item {
                PaButtonPrimary(
                    text = "Primary",
                    onClick = {},
                    leadingIcon = Icons.Default.Favorite.painter(),
                    trailingIcon = Icons.Default.FavoriteBorder.painter(),
                    loading = true,
                )
            }
            item {
                PaButtonSecondary(
                    text = "Secondary",
                    onClick = {},
                    enabled = true,
                    leadingIcon = Icons.Default.Favorite.painter(),
                    trailingIcon = Icons.Default.FavoriteBorder.painter(),
                )
            }
            item {
                PaButtonSecondary(
                    text = "Secondary",
                    onClick = {},
                    enabled = false,
                    leadingIcon = Icons.Default.Favorite.painter(),
                    trailingIcon = Icons.Default.FavoriteBorder.painter(),
                )
            }
            item {
                PaButtonSecondary(
                    text = "Secondary",
                    onClick = {},
                    loading = true,
                    leadingIcon = Icons.Default.Favorite.painter(),
                    trailingIcon = Icons.Default.FavoriteBorder.painter(),
                )
            }
            item {
                PaButtonTertiary(
                    text = "Tertiary",
                    onClick = {},
                    enabled = true,
                    leadingIcon = Icons.Default.Favorite.painter(),
                    trailingIcon = Icons.Default.FavoriteBorder.painter(),
                )
            }
            item {
                PaButtonTertiary(
                    text = "Tertiary",
                    onClick = {},
                    enabled = false,
                    leadingIcon = Icons.Default.Favorite.painter(),
                    trailingIcon = Icons.Default.FavoriteBorder.painter(),
                )
            }
            item {
                PaButtonTertiary(
                    text = "Tertiary",
                    onClick = {},
                    loading = true,
                    leadingIcon = Icons.Default.Favorite.painter(),
                    trailingIcon = Icons.Default.FavoriteBorder.painter(),
                )
            }
        }
    }
}
