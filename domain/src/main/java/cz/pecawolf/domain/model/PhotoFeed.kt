package cz.pecawolf.domain.model

import kotlin.time.Instant

data class PhotoFeed(
    val title: String,
    val link: String,
    val description: String,
    val modified: Instant?,
    val generator: String,
    val items: List<PhotoItem>,
)
