package cz.pecawolf.data.model

import cz.pecawolf.domain.model.PhotoItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoItemDto(
    @SerialName("title")
    val title: String,
    @SerialName("link")
    val link: String,
    @SerialName("media")
    val media: MediaDto,
    @SerialName("date_taken")
    val dateTaken: String,
    @SerialName("description")
    val description: String,
    @SerialName("published")
    val published: String,
    @SerialName("author")
    val author: String,
    @SerialName("author_id")
    val authorId: String,
    @SerialName("tags")
    val tags: String,
)

internal fun PhotoItemDto.toDomain() = PhotoItem(
    title = title,
    link = link,
    imageUrl = media.imageUrl,
    dateTaken = dateTaken,
    description = description,
    published = published,
    author = author,
    authorId = authorId,
    tags = tags.split(" ").filter { it.isNotBlank() },
)