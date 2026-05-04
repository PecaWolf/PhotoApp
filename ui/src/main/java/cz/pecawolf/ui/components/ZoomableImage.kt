package cz.pecawolf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import coil3.compose.AsyncImage
import io.github.aakira.napier.Napier

const val ZOOM_MIN = 1f
const val ZOOM_MAX = 10f
const val ZOOM_DEFAULT = (ZOOM_MAX - ZOOM_MIN) / 2f

@Composable
fun ZoomableImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    defaultPan: Offset = Offset.Zero,
    defaultZoom: Float = ZOOM_DEFAULT,
    defaultRotation: Float = 0f,
    resetKey: Int = 0,
    onStateChange: (Offset, Float, Float) -> Unit = { _, _, _ -> },
    onImageLoaded: (Int, Int, Int, Int) -> Unit = { _, _, _, _ -> },
) {
    val panState = remember { mutableStateOf(defaultPan) }
    val zoomState = remember { mutableStateOf(defaultZoom) }
    val rotationState = remember { mutableStateOf(defaultRotation) }

    LaunchedEffect(resetKey) {
        panState.value = defaultPan
        zoomState.value = defaultZoom
        rotationState.value = defaultRotation
    }

    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current

    val screenHeight = with(density) {
        windowInfo.containerDpSize.height.roundToPx()
    }
    val screenWidth = with(density) {
        windowInfo.containerDpSize.width.roundToPx()
    }

    Box(
        modifier = modifier
            .clip(RectangleShape)
            .fillMaxSize()
            .background(Color.Gray)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    panState.value += pan
                    zoomState.value *= zoom
                    rotationState.value += rotation

                    onStateChange(panState.value, zoomState.value, rotationState.value)
                }
            }
    ) {
        AsyncImage(
            modifier = Modifier.run {
                align(Alignment.Center) // keep the image centralized into the Box
                    .graphicsLayer(
                        translationX = panState.value.x,
                        translationY = panState.value.y,
                        scaleX = zoomState.value.coerceIn(
                            ZOOM_MIN,
                            ZOOM_MAX
                        ),
                        scaleY = zoomState.value.coerceIn(
                            ZOOM_MIN,
                            ZOOM_MAX
                        ),
                        rotationZ = rotationState.value
                    )
            },
            model = imageUrl,
            contentDescription = null,
            onSuccess = {
                val imageWidth = it.result.image.width
                val imageHeight = it.result.image.height

                Napier.d { "ZoomableImage: onSuccess: $imageWidth x $imageHeight" }
                onImageLoaded(
                    screenWidth,
                    screenHeight,
                    imageWidth,
                    imageHeight
                )
            },
        )
    }
}
