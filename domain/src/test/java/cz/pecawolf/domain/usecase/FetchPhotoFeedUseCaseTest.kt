package cz.pecawolf.domain.usecase

import cz.pecawolf.domain.model.PhotoFeed
import cz.pecawolf.domain.model.PhotoItem
import cz.pecawolf.domain.repository.PhotoRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class FetchPhotoFeedUseCaseTest {

    private val mockRepository: PhotoRepository = mock()
    private val useCase = FetchPhotoFeedUseCase(mockRepository)

    @Test
    fun `invoke returns success result from repository`() = runTest {
        val expectedFeed = createPhotoFeed()
        whenever(mockRepository.getPhotoFeed(listOf("nature"), true))
            .thenReturn(Result.success(expectedFeed))

        val result = useCase(tags = listOf("nature"), matchAllTags = true)

        assertTrue(result.isSuccess)
        assertEquals(expectedFeed, result.getOrNull())
        verify(mockRepository).getPhotoFeed(listOf("nature"), true)
    }

    @Test
    fun `invoke returns failure result from repository`() = runTest {
        val exception = RuntimeException("Network error")
        whenever(mockRepository.getPhotoFeed(emptyList(), false))
            .thenReturn(Result.failure(exception))

        val result = useCase(tags = emptyList(), matchAllTags = false)

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
        verify(mockRepository).getPhotoFeed(emptyList(), false)
    }

    @Test
    fun `invoke passes correct parameters to repository`() = runTest {
        val tags = listOf("sky", "sunset")
        val matchAllTags = false
        whenever(mockRepository.getPhotoFeed(tags, matchAllTags))
            .thenReturn(Result.success(createPhotoFeed()))

        useCase(tags = tags, matchAllTags = matchAllTags)

        verify(mockRepository).getPhotoFeed(tags, matchAllTags)
    }

    private fun createPhotoFeed(
        title: String = "Test Title",
        items: List<PhotoItem> = listOf(createPhotoItem()),
    ) = PhotoFeed(
        title = title,
        link = "https://example.com",
        description = "Test description",
        modified = null,
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
}
