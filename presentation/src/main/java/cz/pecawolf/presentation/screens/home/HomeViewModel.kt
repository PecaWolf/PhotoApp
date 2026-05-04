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
            is Event.OnShowListChange -> handleShowListChange(event.isChecked)
            is Event.OnSearchQueryChange -> handleSearchQueryChange(event.query)
            is Event.OnDeleteTagClick -> handleDeleteTagClick(event.tag)
            is Event.OnAllTagsChange -> handleAllTagsChange(event.isChecked)
        }
    }

    private fun handleItemClick(photo: PhotoItem) {
        Napier.d { "handleItemClick(): $photo" }
        viewModelScope.launch {
            _effect.send(Effect.NavigateToItemDetail(photo))
        }
    }

    private fun handleFullScreenClick(photo: PhotoItem) {
        Napier.d { "handleFullScreenClick(): $photo" }
        viewModelScope.launch {
            _effect.send(Effect.NavigateToItemFullScreen(photo))
        }
    }

    private fun handleShowListChange(isChecked: Boolean) {
        Napier.d { "handleOnShowListChange(): $isChecked" }
        _uiState.update {
            it.copy(showList = isChecked)
        }
    }

    private fun handleSearchQueryChange(query: String) {
        Napier.d { "handleOnSearchQueryChange(): $query" }
        _uiState.update {
            it.copy(searchQuery = query)
        }
    }

    private fun handleDeleteTagClick(tag: String) {
        Napier.d { "handleDeleteTagClick(): $tag" }
        _uiState.update {
            it.copy(
                searchQuery = it.searchTags.filter { it != tag }.joinToString(" "),
            )
        }
    }

    private fun handleAllTagsChange(isChecked: Boolean) {
        Napier.d { "handleAllTagsToggle(): $isChecked" }
        _uiState.update {
            it.copy(
                matchAllTags = isChecked,
            )
        }
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
        val showList: Boolean = true,
        val searchQuery: String = "",
        val searchExpanded: Boolean = false,
        val matchAllTags: Boolean = true,
    ) {
        val searchTags: List<String>
            get() = searchQuery
                .split(" ")
                .filter { it.isNotBlank() }
    }

    sealed interface Event {
        data object Refresh : Event
        data class PhotoClick(val photo: PhotoItem) : Event
        data class PhotoFullScreenClick(val photo: PhotoItem) : Event
        data class OnShowListChange(val isChecked: Boolean) : Event
        data class OnSearchQueryChange(val query: String) : Event
        data class OnDeleteTagClick(val tag: String) : Event
        class OnAllTagsChange(val isChecked: Boolean) : Event
    }

    sealed interface Effect {
        data class NavigateToItemDetail(val photo: PhotoItem) : Effect
        data class NavigateToItemFullScreen(val photo: PhotoItem) : Effect
    }
}
