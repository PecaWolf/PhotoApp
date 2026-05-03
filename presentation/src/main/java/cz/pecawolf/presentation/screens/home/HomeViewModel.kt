package cz.pecawolf.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.pecawolf.domain.model.PhotoItem
import cz.pecawolf.domain.usecase.FetchPhotosUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val fetchPhotos: FetchPhotosUseCase,
) : ViewModel() {

    private val _effect = Channel<Effect>()
    private val _uiState = MutableStateFlow(UiState())

    val effect = _effect.receiveAsFlow()
    val uiState: StateFlow<UiState> = _uiState

    init {
        loadPhotos()
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.Refresh -> loadPhotos()
            is Event.PhotoClick -> handleItemClick(event.photo)
        }
    }

    private fun handleItemClick(photo: PhotoItem) {

    }

    private fun loadPhotos() {
        viewModelScope.launch {
            fetchPhotos()
                .fold(
                    onSuccess = { onFetchPhotosSuccess(it) },
                    onFailure = { onFetchPhotosFailure(it) },
                )
        }
    }

    private fun onFetchPhotosSuccess(photos: List<PhotoItem>) {
        Napier.d("onFetchPhotosSuccess(): ${photos.size}")
        _uiState.update {
            it.copy(
                loading = false,
                photos = photos,
                error = null,
            )
        }
    }

    private fun onFetchPhotosFailure(error: Throwable) {
        Napier.w("onFetchPhotosFailure(): ", error)
        _uiState.update {
            it.copy(
                loading = false,
                error = error.message,
            )
        }
    }

    data class UiState(
        val loading: Boolean = true,
        val photos: List<PhotoItem> = emptyList(),
        val error: String? = null,
    )

    sealed interface Event {
        data object Refresh : Event
        data class PhotoClick(val photo: PhotoItem) : Event
    }

    sealed interface Effect {
    }
}