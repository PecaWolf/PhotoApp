package cz.pecawolf.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.pecawolf.domain.model.PhotoFeed
import cz.pecawolf.domain.model.PhotoItem
import cz.pecawolf.domain.usecase.FetchPhotoFeedUseCase
import cz.pecawolf.presentation.formatDateTimeBySystemLocale
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val fetchPhotoFeed: FetchPhotoFeedUseCase,
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
            is Event.PhotoFullScreenClick -> handleFullScreenClick(event.photo)
        }
    }

    private fun handleItemClick(photo: PhotoItem) {
        Napier.d { "handleItemClick(): $photo" }

    }

    private fun handleFullScreenClick(photo: PhotoItem) {
        Napier.d { "handleFullScreenClick(): $photo" }

    }

    private fun loadPhotos() {
        _uiState.update {
            it.copy(loading = true)
        }
        viewModelScope.launch {
            fetchPhotoFeed()
                .fold(
                    onSuccess = { onFetchPhotoFeedSuccess(it) },
                    onFailure = { onFetchPhotoFeedFailure(it) },
                )
        }
    }

    private fun onFetchPhotoFeedSuccess(photos: PhotoFeed) {
        Napier.d("onFetchPhotoFeedSuccess(): ${photos.items.size}")
        _uiState.update {
            it.copy(
                loading = false,
                title = photos.title,
                subtitle = photos.modified?.formatDateTimeBySystemLocale(),
                photos = photos.items,
                error = null,
            )
        }
    }

    private fun onFetchPhotoFeedFailure(error: Throwable) {
        Napier.w("onFetchPhotoFeedFailure(): ", error)
        _uiState.update {
            it.copy(
                loading = false,
                error = error.message,
            )
        }
    }

    data class UiState(
        val loading: Boolean = true,
        val title: String = "",
        val subtitle: String? = null,
        val photos: List<PhotoItem> = emptyList(),
        val error: String? = null,
    )

    sealed interface Event {
        data object Refresh : Event
        data class PhotoClick(val photo: PhotoItem) : Event
        data class PhotoFullScreenClick(val photo: PhotoItem) : Event
    }

    sealed interface Effect {
    }
}
