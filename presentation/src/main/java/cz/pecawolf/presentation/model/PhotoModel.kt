package cz.pecawolf.presentation.model

import cz.pecawolf.domain.model.PhotoItem

data class PhotoModel(
    val title: String,
    val link: String,
    val imageUrl: String,
    val dateTaken: String,
    val description: String,
    val published: String,
    val author: String,
    val authorId: String,
    val tags: List<String>,
)

fun PhotoItem.toPresentation() = PhotoModel(
    title = title,
    link = link,
    imageUrl = imageUrl,
    dateTaken = dateTaken,
    description = description,
    published = published,
    author = author,
    authorId = authorId,
    tags = tags,
)