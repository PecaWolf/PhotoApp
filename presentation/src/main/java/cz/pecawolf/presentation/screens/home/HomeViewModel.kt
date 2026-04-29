package cz.pecawolf.presentation.screens.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class HomeViewModel() : ViewModel() {

    private val _effect = Channel<Effect>()
    private val _uiState = MutableStateFlow(UiState())

    val effect = _effect.receiveAsFlow()
    val uiState: StateFlow<UiState> = _uiState

    fun onEvent(event: Event) {
        when (event) {
            is Event.ItemClick -> handleItemClick(event.item)
        }
    }

    private fun handleItemClick(item: String) {

    }

    data class UiState(val items: List<String> = emptyList())

    sealed interface Event {
        data class ItemClick(val item: String) : Event

    }

    sealed interface Effect {
        data object NavigateBack : Effect
    }
}