package cz.pecawolf.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.pecawolf.domain.model.PhotoFeed
import cz.pecawolf.domain.model.PhotoItem
import cz.pecawolf.domain.usecase.EncodeUrlUseCase
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
    private val encodeUrl: EncodeUrlUseCase,
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
            is Event.PhotoDetailDismiss -> handlePhotoDetailDismiss()
        }
    }

    private fun handleItemClick(photo: PhotoItem) {
        Napier.d { "handleItemClick(): $photo" }
        _uiState.update {
            it.copy(
                displayedDetail = photo,
            )
        }
    }

    private fun handleFullScreenClick(photo: PhotoItem) {
        Napier.d { "handleFullScreenClick(): ${photo.imageUrl}" }
        viewModelScope.launch {
            _effect.send(Effect.NavigateToItemFullScreen(imageUrl = encodeUrl(photo.imageUrl)))
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
        handleSearchParametersChange(
            tags = newState.searchTags,
            matchAllTags = newState.matchAllTags,
        )
    }

    private fun handleDeleteTagClick(tag: String) {
        Napier.d { "handleDeleteTagClick(): $tag" }
        val newState = _uiState.updateAndGet {
            it.copy(
                searchQuery = it.searchTags.filter { it != tag }.joinToString(" "),
            )
        }
        handleSearchParametersChange(
            tags = newState.searchTags,
            matchAllTags = newState.matchAllTags,
        )
    }

    private fun handleAllTagsChange(isChecked: Boolean) {
        Napier.d { "handleAllTagsToggle(): $isChecked" }
        val newState = _uiState.updateAndGet {
            it.copy(
                matchAllTags = isChecked,
            )
        }
        if (newState.searchTags.size > 1) {
            handleSearchParametersChange(
                tags = newState.searchTags,
                matchAllTags = newState.matchAllTags,
            )
        }
    }

    private fun handleSearchParametersChange(
        tags: List<String>,
        matchAllTags: Boolean,
    ) {
        Napier.v { "handleSearchParametersChange(): $tags, $matchAllTags" }
        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(0.5.seconds)
            loadPhotos(
                tags = tags,
                matchAllTags = matchAllTags,
            )
        }
    }

    private fun handlePhotoDetailDismiss() {
        Napier.d { "handlePhotoDetailDismiss()" }
        _uiState.update {
            it.copy(
                displayedDetail = null,
            )
        }
    }

    private fun loadPhotos(
        tags: List<String> = emptyList(),
        matchAllTags: Boolean = true,
    ) {
        Napier.v { "loadPhotos(): $tags, $matchAllTags" }
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
        val displayedDetail: PhotoItem? = null,
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
        data object PhotoDetailDismiss : Event
    }

    sealed interface Effect {
        data class NavigateToItemFullScreen(val imageUrl: String) : Effect
    }
}
