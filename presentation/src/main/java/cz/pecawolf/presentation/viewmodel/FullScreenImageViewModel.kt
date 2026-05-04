package cz.pecawolf.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val ZOOM_MIN = 1f
const val ZOOM_MAX = 10f
const val ZOOM_DEFAULT = (ZOOM_MAX - ZOOM_MIN) / 2f

class FullScreenImageViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val imageUrl: String? = savedStateHandle.get<String>("imageUrl")

    private val _effect = Channel<Effect>()
    private val _uiState = MutableStateFlow(UiState(imageUrl = imageUrl))

    val effect = _effect.receiveAsFlow()
    val uiState: StateFlow<UiState> = _uiState

    fun onEvent(event: Event) {
        when (event) {
            is Event.BackClick -> viewModelScope.launch { _effect.send(Effect.NavigateBack) }
            is Event.ImageStateChange -> handleImageStateChange(
                event.panX,
                event.panY,
                event.zoom,
                event.rotation
            )

            is Event.ResetImage -> handleResetImage()
        }
    }

    private fun handleImageStateChange(
        panX: Float,
        panY: Float,
        zoom: Float,
        rotation: Float,
    ) {
        Napier.d { "handleImageStateChange(): $panX, $panY, $zoom, $rotation" }
        _uiState.update {
            it.copy(
                panX = panX,
                panY = panY,
                zoom = zoom,
                rotation = rotation,
            )
        }
    }

    private fun handleResetImage() {
        Napier.d { "handleResetImage()" }
        _uiState.update {
            it.copy(
                panX = 0f,
                panY = 0f,
                zoom = ZOOM_DEFAULT,
                rotation = 0f,
            )
        }
    }

    data class UiState(
        val imageUrl: String? = null,
        val panX: Float = 0f,
        val panY: Float = 0f,
        val zoom: Float = ZOOM_DEFAULT,
        val zoomMin: Float = ZOOM_MIN,
        val zoomMax: Float = ZOOM_MAX,
        val rotation: Float = 0f,
    ) {
        val isResetEnabled: Boolean
            get() = panX != 0f || panY != 0f || zoom != ZOOM_DEFAULT || rotation != 0f
    }

    sealed interface Event {
        data object BackClick : Event
        data class ImageStateChange(
            val panX: Float,
            val panY: Float,
            val zoom: Float,
            val rotation: Float,
        ) : Event

        data object ResetImage : Event
    }

    sealed interface Effect {
        data object NavigateBack : Effect
    }
}