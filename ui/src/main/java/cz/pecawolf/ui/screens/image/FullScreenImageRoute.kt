package cz.pecawolf.ui.screens.image

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cz.pecawolf.ui.components.Dimensions
import cz.pecawolf.ui.components.PaBackIconButton
import cz.pecawolf.ui.components.PaIconButtonPrimary
import cz.pecawolf.ui.components.ZoomableImage
import cz.pecawolf.ui.components.painter
import cz.pecawolf.ui.components.string
import cz.pecawolf.presentation.R
import cz.pecawolf.presentation.viewmodel.FullScreenImageViewModel
import cz.pecawolf.presentation.viewmodel.FullScreenImageViewModel.UiState
import cz.pecawolf.presentation.viewmodel.FullScreenImageViewModel.Effect
import cz.pecawolf.presentation.viewmodel.FullScreenImageViewModel.Event
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
                        text = R.string.fullscreen_no_image_selected.string(),
                    )

                    else -> ZoomableImage(
                        modifier = Modifier.fillMaxSize(),
                        imageUrl = uiState.imageUrl.orEmpty(),
                        defaultPan = Offset(
                            x = uiState.panX,
                            y = uiState.panY,
                        ),
                        defaultZoom = uiState.zoom,
                        defaultRotation = uiState.rotation,
                        onStateChange = { pan, zoom, rotation ->
                            onEvent(
                                Event.ImageStateChange(
                                    panX = pan.x,
                                    panY = pan.y,
                                    zoom = zoom,
                                    rotation = rotation,
                                )
                            )
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

            if (uiState.isResetEnabled) {
                PaIconButtonPrimary(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(all = Dimensions.spaceSmall),
                    painter = Icons.Default.Refresh.painter(),
                    onClick = { onEvent(Event.ResetImage) },
                    contentDescription = R.string.fullscreen_reset_photo_button_content_description.string(),
                )
            }
        }
    }
}
