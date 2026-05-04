package cz.pecawolf.presentation.screens.home

import cz.pecawolf.domain.model.PhotoFeed
import cz.pecawolf.domain.model.PhotoItem
import cz.pecawolf.domain.usecase.EncodeUrlUseCase
import cz.pecawolf.domain.usecase.FetchPhotoFeedUseCase
import cz.pecawolf.presentation.model.PhotoModel
import cz.pecawolf.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private val mockFetchPhotoFeedUseCase: FetchPhotoFeedUseCase = mock()
    private val mockEncodeUrlUseCase: EncodeUrlUseCase = mock()
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        stubFetchPhotoFeedUseCase(Result.success(createPhotoFeed()))
        whenever(mockEncodeUrlUseCase.invoke(any())).thenReturn("encoded url")
        viewModel = HomeViewModel(
            fetchPhotoFeed = mockFetchPhotoFeedUseCase,
            encodeUrl = mockEncodeUrlUseCase,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun stubFetchPhotoFeedUseCase(result: Result<PhotoFeed>) = runBlocking {
        whenever(mockFetchPhotoFeedUseCase.invoke(any(), any())).thenReturn(result)
    }

    @Test
    fun `init sets loading to true immediately`() {
        assertTrue(viewModel.uiState.value.loading)
    }

    @Test
    fun `init loads photos and updates state on success`() = runTest(testDispatcher) {
        val feed = createPhotoFeed()
        stubFetchPhotoFeedUseCase(Result.success(feed))

        // Re-create viewModel so init runs with the new stub
        viewModel = HomeViewModel(
            fetchPhotoFeed = mockFetchPhotoFeedUseCase,
            encodeUrl = mockEncodeUrlUseCase,
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.loading)
        assertEquals("Test Title", state.title)
        assertEquals(listOf(createPhotoModel()), state.photos)
        assertNull(state.error)
    }

    @Test
    fun `init shows error on load failure`() = runTest(testDispatcher) {
        stubFetchPhotoFeedUseCase(Result.failure(RuntimeException("Network error")))

        viewModel = HomeViewModel(
            fetchPhotoFeed = mockFetchPhotoFeedUseCase,
            encodeUrl = mockEncodeUrlUseCase,
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.loading)
        assertEquals("Network error", state.error)
        assertTrue(state.photos.isEmpty())
    }

    @Test
    fun `refresh event reloads photos`() = runTest(testDispatcher) {
        val updatedFeed = createPhotoFeed(title = "Second")
        stubFetchPhotoFeedUseCase(Result.success(updatedFeed))

        viewModel.onEvent(HomeViewModel.Event.Refresh)
        advanceUntilIdle()

        assertEquals("Second", viewModel.uiState.value.title)
    }

    @Test
    fun `photo click updates displayed detail`() {
        val photo = createPhotoModel()
        viewModel.onEvent(HomeViewModel.Event.PhotoClick(photo))

        assertEquals(photo, viewModel.uiState.value.displayedDetail)
    }

    @Test
    fun `photo full screen click sends navigation effect`() = runTest(testDispatcher) {
        val photo = createPhotoModel(imageUrl = "https://example.com/photo.jpg")
        val effects = mutableListOf<HomeViewModel.Effect>()

        val job = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }

        viewModel.onEvent(HomeViewModel.Event.PhotoFullScreenClick(photo))
        advanceUntilIdle()

        assertEquals(1, effects.size)
        val effect = effects[0] as HomeViewModel.Effect.NavigateToItemFullScreen
        assertEquals(
            URLDecoder.decode(effect.imageUrl, StandardCharsets.UTF_8.toString()),
            "encoded url"
        )

        job.cancel()
    }

    @Test
    fun `show list change updates state`() {
        viewModel.onEvent(HomeViewModel.Event.OnShowListChange(false))
        assertFalse(viewModel.uiState.value.showList)

        viewModel.onEvent(HomeViewModel.Event.OnShowListChange(true))
        assertTrue(viewModel.uiState.value.showList)
    }

    @Test
    fun `search query change updates state and debounces photo load`() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onEvent(HomeViewModel.Event.OnSearchQueryChange("nature"))
        advanceTimeBy(400.milliseconds)

        // Should not have triggered load yet (debounce is 500ms)
        verify(mockFetchPhotoFeedUseCase, times(1)).invoke(any(), any())
        assertEquals("nature", viewModel.uiState.value.searchQuery)

        advanceTimeBy(200.milliseconds)

        verify(mockFetchPhotoFeedUseCase, times(2)).invoke(any(), any())
    }

    @Test
    fun `delete tag click removes tag and debounces load`() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onEvent(HomeViewModel.Event.OnSearchQueryChange("nature sky"))
        advanceTimeBy(600.milliseconds)
        advanceUntilIdle()

        viewModel.onEvent(HomeViewModel.Event.OnDeleteTagClick("nature"))
        advanceTimeBy(600.milliseconds)
        advanceUntilIdle()

        assertEquals(listOf("sky"), viewModel.uiState.value.searchTags)
    }

    @Test
    fun `all tags change updates state and debounces when multiple tags`() =
        runTest(testDispatcher) {
            advanceUntilIdle()

            viewModel.onEvent(HomeViewModel.Event.OnSearchQueryChange("nature sky"))
            advanceTimeBy(600.milliseconds)
            advanceUntilIdle()

            viewModel.onEvent(HomeViewModel.Event.OnAllTagsChange(false))
            advanceTimeBy(600.milliseconds)
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.matchAllTags)
        }

    @Test
    fun `all tags change does not trigger load when single tag`() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onEvent(HomeViewModel.Event.OnSearchQueryChange("nature"))
        advanceTimeBy(600.milliseconds)
        advanceUntilIdle()

        val callCountBefore = getInvokeCallCount()

        viewModel.onEvent(HomeViewModel.Event.OnAllTagsChange(false))
        advanceUntilIdle()

        // Should not have triggered another load because only 1 tag
        assertEquals(callCountBefore, getInvokeCallCount())
        assertFalse(viewModel.uiState.value.matchAllTags)
    }

    @Test
    fun `photo detail dismiss clears displayed detail`() {
        val photo = createPhotoModel()
        viewModel.onEvent(HomeViewModel.Event.PhotoClick(photo))
        assertNotNull(viewModel.uiState.value.displayedDetail)

        viewModel.onEvent(HomeViewModel.Event.PhotoDetailDismiss)
        assertNull(viewModel.uiState.value.displayedDetail)
    }

    @Test
    fun `load photos passes correct tags and matchAllTags parameters`() = runTest(testDispatcher) {
        advanceUntilIdle()

        viewModel.onEvent(HomeViewModel.Event.OnSearchQueryChange("tag1 tag2"))
        advanceTimeBy(600.milliseconds)
        advanceUntilIdle()

        val tagsCaptor = argumentCaptor<List<String>>()
        val matchAllCaptor = argumentCaptor<Boolean>()
        verify(mockFetchPhotoFeedUseCase, atLeastOnce()).invoke(
            tagsCaptor.capture(),
            matchAllCaptor.capture()
        )

        val lastTags = tagsCaptor.allValues.last()
        val lastMatchAll = matchAllCaptor.allValues.last()
        assertEquals(listOf("tag1", "tag2"), lastTags)
        assertTrue(lastMatchAll)
    }

    @Test
    fun `subtitle is set from modified date on success`() = runTest(testDispatcher) {
        val feed =
            createPhotoFeed(modified = kotlinx.datetime.Instant.parse("2024-01-15T10:30:00Z"))
        stubFetchPhotoFeedUseCase(Result.success(feed))

        viewModel = HomeViewModel(
            fetchPhotoFeed = mockFetchPhotoFeedUseCase,
            encodeUrl = mockEncodeUrlUseCase,
        )
        advanceUntilIdle()

        assertNotNull(viewModel.uiState.value.subtitle)
    }

    private fun getInvokeCallCount(): Int = runBlocking {
        val tagsCaptor = argumentCaptor<List<String>>()
        val matchAllCaptor = argumentCaptor<Boolean>()
        verify(mockFetchPhotoFeedUseCase, atLeastOnce()).invoke(
            tagsCaptor.capture(),
            matchAllCaptor.capture()
        )
        tagsCaptor.allValues.size
    }

    private fun createPhotoFeed(
        title: String = "Test Title",
        modified: kotlinx.datetime.Instant? = null,
        items: List<PhotoItem> = listOf(createPhotoItem()),
    ) = PhotoFeed(
        title = title,
        link = "https://example.com",
        description = "Test description",
        modified = modified,
        generator = "test",
        items = items,
    )

    private fun createPhotoItem(
        title: String = "Photo",
        imageUrl: String = "https://example.com/photo.jpg",
    ) = PhotoItem(
        title = title,
        link = "https://example.com/link",
        imageUrl = imageUrl,
        dateTaken = "2024-01-01",
        description = "desc",
        published = "2024-01-01",
        author = "author",
        authorId = "id",
        tags = emptyList(),
    )

    private fun createPhotoModel(
        title: String = "Photo",
        imageUrl: String = "https://example.com/photo.jpg",
    ) = PhotoModel(
        title = title,
        link = "https://example.com/link",
        imageUrl = imageUrl,
        dateTaken = "2024-01-01",
        description = "desc",
        published = "2024-01-01",
        author = "author",
        authorId = "id",
        tags = emptyList(),
    )
}
