package cz.pecawolf.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.pecawolf.domain.model.PhotoFeed
import cz.pecawolf.domain.model.PhotoItem
import cz.pecawolf.domain.usecase.FetchPhotoFeedUseCase
import cz.pecawolf.presentation.formatDateTimeBySystemLocale
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class HomeViewModel(
    private val fetchPhotoFeed: FetchPhotoFeedUseCase,
) : ViewModel() {

    private val _effect = Channel<Effect>()
    private val _uiState = MutableStateFlow(UiState())

    private var debounceJob: Job? = null

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
        val newState = _uiState.updateAndGet {
            it.copy(searchQuery = query)
        }
        handleSearchParametersChange(newState)
    }

    private fun handleDeleteTagClick(tag: String) {
        Napier.d { "handleDeleteTagClick(): $tag" }
        val newState = _uiState.updateAndGet {
            it.copy(
                searchQuery = it.searchTags.filter { it != tag }.joinToString(" "),
            )
        }
        handleSearchParametersChange(newState)
    }

    private fun handleAllTagsChange(isChecked: Boolean) {
        Napier.d { "handleAllTagsToggle(): $isChecked" }
        val newState = _uiState.updateAndGet {
            it.copy(
                matchAllTags = isChecked,
            )
        }
        handleSearchParametersChange(newState)
    }

    private fun handleSearchParametersChange(newState: UiState) {
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(1.seconds)
            loadPhotos(
                tags = newState.searchTags,
                matchAllTags = newState.matchAllTags,
            )
        }
    }

    private fun loadPhotos(
        tags: List<String> = emptyList(),
        matchAllTags: Boolean = true,
    ) {
        _uiState.update {
            it.copy(loading = true)
        }
        viewModelScope.launch {
            fetchPhotoFeed(
                tags = tags,
                matchAllTags = matchAllTags,
            )
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
