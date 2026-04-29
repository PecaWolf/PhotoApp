package cz.pecawolf.domain.model

data class PhotoItem(
    val title: String,
    val link: String,
    val imageUrl: String,
    val dateTaken: String,
    val description: String,
    val published: String,
    val author: String,
    val authorId: String,
    val tags: String,
)