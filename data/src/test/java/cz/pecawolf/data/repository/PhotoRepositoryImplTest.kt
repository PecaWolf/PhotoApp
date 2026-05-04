package cz.pecawolf.data.repository

import cz.pecawolf.data.api.PhotoApi
import cz.pecawolf.data.model.MediaDto
import cz.pecawolf.data.model.PhotoFeedDto
import cz.pecawolf.data.model.PhotoItemDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class PhotoRepositoryImplTest {

    private val photoApi: PhotoApi = mock()
    private lateinit var repository: PhotoRepositoryImpl

    @Before
    fun setup() {
        repository = PhotoRepositoryImpl(photoApi)
    }

    @Test
    fun `getPhotoFeed with empty tags calls api with null parameters and returns mapped domain model`() =
        runTest {
            val dto = createPhotoFeedDto()
            whenever(photoApi.getPhotos(tags = null, tagMode = null)).thenReturn(dto)

            val result = repository.getPhotoFeed(tags = emptyList(), matchAllTags = false)

            assertTrue(result.isSuccess)
            val domain = result.getOrThrow()
            assertEquals(dto.title, domain.title)
            assertEquals(dto.link, domain.link)
            assertEquals(dto.description, domain.description)
            assertEquals(dto.generator, domain.generator)
            assertEquals(1, domain.items.size)
            assertEquals("Photo 1", domain.items[0].title)
        }

    @Test
    fun `getPhotoFeed with tags and matchAllTags true calls api with comma separated tags and all tagmode`() =
        runTest {
            val dto = createPhotoFeedDto()
            whenever(photoApi.getPhotos(tags = "nature,sky", tagMode = "all")).thenReturn(dto)

            val result = repository.getPhotoFeed(
                tags = listOf("nature", "sky"),
                matchAllTags = true
            )

            assertTrue(result.isSuccess)
            verify(photoApi).getPhotos(tags = "nature,sky", tagMode = "all")
        }

    @Test
    fun `getPhotoFeed with tags and matchAllTags false calls api with comma separated tags and any tagmode`() =
        runTest {
            val dto = createPhotoFeedDto()
            whenever(photoApi.getPhotos(tags = "nature,sky", tagMode = "any")).thenReturn(dto)

            val result = repository.getPhotoFeed(
                tags = listOf("nature", "sky"),
                matchAllTags = false
            )

            assertTrue(result.isSuccess)
            verify(photoApi).getPhotos(tags = "nature,sky", tagMode = "any")
        }

    @Test
    fun `getPhotoFeed returns failure when api throws exception`() = runTest {
        val exception = RuntimeException("Network error")
        whenever(photoApi.getPhotos(tags = null, tagMode = null)).thenThrow(exception)

        val result = repository.getPhotoFeed(tags = emptyList(), matchAllTags = false)

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getPhotoFeed maps dto items to domain correctly`() = runTest {
        val dto = createPhotoFeedDto(
            items = listOf(
                PhotoItemDto(
                    title = "Test Photo",
                    link = "https://example.com/link",
                    media = MediaDto(imageUrl = "https://example.com/image.jpg"),
                    dateTaken = "2024-01-01T12:00:00Z",
                    description = "A description",
                    published = "2024-01-01T12:00:00Z",
                    author = "author@example.com",
                    authorId = "12345",
                    tags = "nature sky sunset"
                )
            )
        )
        whenever(photoApi.getPhotos(tags = null, tagMode = null)).thenReturn(dto)

        val result = repository.getPhotoFeed(tags = emptyList(), matchAllTags = false)

        assertTrue(result.isSuccess)
        val item = result.getOrThrow().items[0]
        assertEquals("Test Photo", item.title)
        assertEquals("https://example.com/link", item.link)
        assertEquals("https://example.com/image.jpg", item.imageUrl)
        assertEquals("2024-01-01T12:00:00Z", item.dateTaken)
        assertEquals("A description", item.description)
        assertEquals("2024-01-01T12:00:00Z", item.published)
        assertEquals("author@example.com", item.author)
        assertEquals("12345", item.authorId)
        assertEquals(listOf("nature", "sky", "sunset"), item.tags)
    }

    private fun createPhotoFeedDto(
        title: String = "Test Feed",
        link: String = "https://example.com",
        description: String = "Test description",
        modified: String = "2024-01-15T10:30:00Z",
        generator: String = "test-generator",
        items: List<PhotoItemDto> = listOf(
            PhotoItemDto(
                title = "Photo 1",
                link = "https://example.com/photo1",
                media = MediaDto(imageUrl = "https://example.com/photo1.jpg"),
                dateTaken = "2024-01-01T12:00:00Z",
                description = "Desc 1",
                published = "2024-01-01T12:00:00Z",
                author = "author1",
                authorId = "id1",
                tags = "tag1 tag2"
            )
        )
    ) = PhotoFeedDto(
        title = title,
        link = link,
        description = description,
        modified = modified,
        generator = generator,
        items = items,
    )
}
