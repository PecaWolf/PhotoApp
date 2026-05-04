package cz.pecawolf.presentation.screens.image

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.pecawolf.presentation.components.ZOOM_DEFAULT
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
                event.pan,
                event.zoom,
                event.rotation
            )

            is Event.ResetImage -> handleResetImage()
        }
    }

    private fun handleImageStateChange(
        pan: Offset,
        zoom: Float,
        rotation: Float,
    ) {
        Napier.d { "handleImageStateChange(): $pan, $zoom, $rotation" }
        _uiState.update {
            it.copy(
                pan = pan,
                zoom = zoom,
                rotation = rotation,
            )
        }
    }

    private fun handleResetImage() {
        Napier.d { "handleResetImage()" }
        _uiState.update {
            it.copy(
                pan = Offset.Zero,
                zoom = ZOOM_DEFAULT,
                rotation = 0f,
            )
        }
    }

    data class UiState(
        val imageUrl: String? = null,
        val pan: Offset = Offset.Zero,
        val zoom: Float = ZOOM_DEFAULT,
        val rotation: Float = 0f,
    ) {
        val isResetEnabled: Boolean
            get() = pan != Offset.Zero || zoom != ZOOM_DEFAULT || rotation != 0f
    }

    sealed interface Event {
        data object BackClick : Event
        data class ImageStateChange(
            val pan: Offset,
            val zoom: Float,
            val rotation: Float,
        ) : Event

        data object ResetImage : Event
    }

    sealed interface Effect {
        data object NavigateBack : Effect
    }
}