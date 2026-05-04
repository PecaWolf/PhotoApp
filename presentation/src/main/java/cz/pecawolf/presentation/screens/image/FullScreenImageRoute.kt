package cz.pecawolf.presentation.screens.image

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cz.pecawolf.presentation.components.Dimensions
import cz.pecawolf.presentation.components.PaBackIconButton
import cz.pecawolf.presentation.components.PaIconButtonPrimary
import cz.pecawolf.presentation.components.ZoomableImage
import cz.pecawolf.presentation.components.painter
import cz.pecawolf.presentation.screens.image.FullScreenImageViewModel.UiState
import cz.pecawolf.presentation.screens.image.FullScreenImageViewModel.Effect
import cz.pecawolf.presentation.screens.image.FullScreenImageViewModel.Event
import org.koin.androidx.compose.koinViewModel

@Composable
fun FullScreenImageRoute(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: FullScreenImageViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect {
            when (it) {
                is Effect.NavigateBack -> onNavigateBack()
            }
        }
    }

    BackHandler { viewModel.onEvent(Event.BackClick) }

    FullScreenImageScreen(
        modifier = modifier,
        uiState = uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
private fun FullScreenImageScreen(
    uiState: UiState,
    modifier: Modifier = Modifier,
    onEvent: (Event) -> Unit,
) {
    Scaffold(
        modifier = modifier,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ) {
            AnimatedContent(
                modifier = Modifier
                    .fillMaxSize(),
                targetState = uiState.imageUrl,
                contentAlignment = Alignment.Center,
            ) {
                when (it) {
                    null -> CircularProgressIndicator(
                        modifier = Modifier.fillMaxSize(0.5f),
                        strokeWidth = 8.dp,
                    )

                    "" -> Text(
                        text = "No image selected",
                    )

                    else -> ZoomableImage(
                        modifier = Modifier.fillMaxSize(),
                        imageUrl = uiState.imageUrl.orEmpty(),
                        defaultPan = uiState.pan,
                        defaultZoom = uiState.zoom,
                        defaultRotation = uiState.rotation,
                        onStateChange = { pan, zoom, rotation ->
                            onEvent(Event.ImageStateChange(pan, zoom, rotation))
                        },
                    )
                }
            }

            PaBackIconButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(all = Dimensions.spaceSmall),
                onClick = { onEvent(Event.BackClick) },
                backgroundColor = Color.Black.copy(alpha = 0.5f),
            )

            if(uiState.isResetEnabled) {
                PaIconButtonPrimary(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(all = Dimensions.spaceSmall),
                    painter = Icons.Default.Refresh.painter(),
                    onClick = { onEvent(Event.ResetImage) },
                    contentDescription = "Reset photo button",
                )
            }
        }
    }
}
