package cz.pecawolf.presentation.screens.image

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.SavedStateHandle
import cz.pecawolf.presentation.components.ZOOM_DEFAULT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class FullScreenImageViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockSavedStateHandle: SavedStateHandle = mock()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init sets imageUrl from SavedStateHandle`() {
        whenever(mockSavedStateHandle.get<String>("imageUrl")).thenReturn("https://example.com/photo.jpg")

        val viewModel = createViewModel()

        assertEquals("https://example.com/photo.jpg", viewModel.uiState.value.imageUrl)
    }

    @Test
    fun `init sets default image state`() {
        whenever(mockSavedStateHandle.get<String>("imageUrl")).thenReturn(null)

        val viewModel = createViewModel()
        val state = viewModel.uiState.value

        assertEquals(Offset.Zero, state.pan)
        assertEquals(ZOOM_DEFAULT, state.zoom)
        assertEquals(0f, state.rotation)
    }

    @Test
    fun `isResetEnabled is false by default`() {
        whenever(mockSavedStateHandle.get<String>("imageUrl")).thenReturn(null)

        val viewModel = createViewModel()

        assertFalse(viewModel.uiState.value.isResetEnabled)
    }

    @Test
    fun `back click sends navigate back effect`() = runTest(testDispatcher) {
        whenever(mockSavedStateHandle.get<String>("imageUrl")).thenReturn(null)

        val viewModel = createViewModel()
        val effects = mutableListOf<FullScreenImageViewModel.Effect>()

        val job = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }

        viewModel.onEvent(FullScreenImageViewModel.Event.BackClick)
        advanceUntilIdle()

        assertEquals(1, effects.size)
        assertTrue(effects[0] is FullScreenImageViewModel.Effect.NavigateBack)

        job.cancel()
    }

    @Test
    fun `image state change updates pan zoom and rotation`() {
        whenever(mockSavedStateHandle.get<String>("imageUrl")).thenReturn(null)

        val viewModel = createViewModel()
        val newPan = Offset(100f, 200f)
        val newZoom = 2.5f
        val newRotation = 45f

        viewModel.onEvent(
            FullScreenImageViewModel.Event.ImageStateChange(
                pan = newPan,
                zoom = newZoom,
                rotation = newRotation
            )
        )

        val state = viewModel.uiState.value
        assertEquals(newPan, state.pan)
        assertEquals(newZoom, state.zoom)
        assertEquals(newRotation, state.rotation)
    }

    @Test
    fun `isResetEnabled is true after image state change`() {
        whenever(mockSavedStateHandle.get<String>("imageUrl")).thenReturn(null)

        val viewModel = createViewModel()

        viewModel.onEvent(
            FullScreenImageViewModel.Event.ImageStateChange(
                pan = Offset(10f, 10f),
                zoom = ZOOM_DEFAULT,
                rotation = 0f
            )
        )

        assertTrue(viewModel.uiState.value.isResetEnabled)
    }

    @Test
    fun `reset image resets state to defaults`() {
        whenever(mockSavedStateHandle.get<String>("imageUrl")).thenReturn(null)

        val viewModel = createViewModel()

        viewModel.onEvent(
            FullScreenImageViewModel.Event.ImageStateChange(
                pan = Offset(100f, 200f),
                zoom = 3f,
                rotation = 90f
            )
        )

        viewModel.onEvent(FullScreenImageViewModel.Event.ResetImage)

        val state = viewModel.uiState.value
        assertEquals(Offset.Zero, state.pan)
        assertEquals(ZOOM_DEFAULT, state.zoom)
        assertEquals(0f, state.rotation)
    }

    @Test
    fun `isResetEnabled is false after reset`() {
        whenever(mockSavedStateHandle.get<String>("imageUrl")).thenReturn(null)

        val viewModel = createViewModel()

        viewModel.onEvent(
            FullScreenImageViewModel.Event.ImageStateChange(
                pan = Offset(10f, 10f),
                zoom = 2f,
                rotation = 0f
            )
        )
        assertTrue(viewModel.uiState.value.isResetEnabled)

        viewModel.onEvent(FullScreenImageViewModel.Event.ResetImage)

        assertFalse(viewModel.uiState.value.isResetEnabled)
    }

    @Test
    fun `isResetEnabled is true when only zoom is changed`() {
        whenever(mockSavedStateHandle.get<String>("imageUrl")).thenReturn(null)

        val viewModel = createViewModel()

        viewModel.onEvent(
            FullScreenImageViewModel.Event.ImageStateChange(
                pan = Offset.Zero,
                zoom = ZOOM_DEFAULT + 0.5f,
                rotation = 0f
            )
        )

        assertTrue(viewModel.uiState.value.isResetEnabled)
    }

    @Test
    fun `isResetEnabled is true when only rotation is changed`() {
        whenever(mockSavedStateHandle.get<String>("imageUrl")).thenReturn(null)

        val viewModel = createViewModel()

        viewModel.onEvent(
            FullScreenImageViewModel.Event.ImageStateChange(
                pan = Offset.Zero,
                zoom = ZOOM_DEFAULT,
                rotation = 15f
            )
        )

        assertTrue(viewModel.uiState.value.isResetEnabled)
    }

    private fun createViewModel(): FullScreenImageViewModel {
        return FullScreenImageViewModel(savedStateHandle = mockSavedStateHandle)
    }
}
