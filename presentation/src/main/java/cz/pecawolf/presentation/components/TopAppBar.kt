package cz.pecawolf.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import cz.pecawolf.presentation.theme.PhotoAppTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PaTopAppBar(
    title: String?,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    alignment: Alignment.Horizontal = Alignment.Start,
    containerColor: Color? = null,
    contentColor: Color? = null,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = alignment,
                verticalArrangement = Arrangement.spacedBy(
                    space = Dimensions.spaceXSmall,
                    alignment = Alignment.CenterVertically,
                ),
            ) {
                title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        navigationIcon = navigationIcon,
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor ?: MaterialTheme.colorScheme.surface,
            titleContentColor = contentColor ?: MaterialTheme.colorScheme.onSurface,
        ),
    )
}

@Preview
@Composable
private fun PaTopAppBarTitleOnlyPreview() {
    PhotoAppTheme {
        PaTopAppBar(
            title = "Title",
        )
    }
}

@Preview
@Composable
private fun PaTopAppBarTitleSubtitlePreview() {
    PhotoAppTheme {
        PaTopAppBar(
            title = "Title",
            subtitle = "Subtitle",
        )
    }
}

@Preview
@Composable
private fun PaTopAppBarBasicPreview() {
    PhotoAppTheme {
        PaTopAppBar(
            title = "Title",
            subtitle = "Subtitle",
            navigationIcon = { PaBackIconButton {} },
        )
    }
}