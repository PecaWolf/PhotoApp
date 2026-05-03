package cz.pecawolf.data.model

import cz.pecawolf.data.toInstant
import cz.pecawolf.domain.model.PhotoFeed
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoFeedDto(
    @SerialName("title")
    val title: String,
    @SerialName("link")
    val link: String,
    @SerialName("description")
    val description: String,
    @SerialName("modified")
    val modified: String,
    @SerialName("generator")
    val generator: String,
    @SerialName("items")
    val items: List<PhotoItemDto>,
)

fun PhotoFeedDto.toDomain() = PhotoFeed(
    title = title,
    link = link,
    description = description,
    modified = modified.toInstant(),
    generator = generator,
    items = items.map(PhotoItemDto::toDomain),
)
