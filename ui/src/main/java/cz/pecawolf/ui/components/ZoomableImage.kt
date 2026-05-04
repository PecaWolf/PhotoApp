package cz.pecawolf.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
import coil3.compose.AsyncImage

const val ZOOM_MIN = 1f
const val ZOOM_MAX = 10f
const val ZOOM_DEFAULT = (ZOOM_MAX - ZOOM_MIN) / 2f

@Composable
fun ZoomableImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
    onStateChange: (Offset, Float, Float) -> Unit = { _, _, _ -> },
    defaultPan: Offset = Offset.Zero,
    defaultZoom: Float = ZOOM_DEFAULT,
    defaultRotation: Float = 0f,
) {
    val offset = remember(defaultPan) { mutableStateOf(defaultPan) }
    val scale = remember(defaultZoom) { mutableStateOf(defaultZoom) }
    val rotationState = remember(defaultRotation) { mutableStateOf(defaultRotation) }
    Box(
        modifier = modifier
            .clip(RectangleShape)
            .fillMaxSize()
            .background(Color.Gray)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, rotation ->
                    offset.value += pan
                    scale.value *= zoom
                    rotationState.value += rotation

                    onStateChange(offset.value, scale.value, rotationState.value)
                }
            }
    ) {
        AsyncImage(
            modifier = Modifier.run {
                align(Alignment.Center) // keep the image centralized into the Box
                    .graphicsLayer(
                        translationX = offset.value.x,
                        translationY = offset.value.y,
                        scaleX = scale.value.coerceIn(
                            ZOOM_MIN,
                            ZOOM_MAX
                        ),
                        scaleY = scale.value.coerceIn(
                            ZOOM_MIN,
                            ZOOM_MAX
                        ),
                        rotationZ = rotationState.value
                    )
            },
            model = imageUrl,
            contentDescription = null,
        )
    }
}
