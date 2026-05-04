package cz.pecawolf.data.repository

import cz.pecawolf.data.api.PhotoApi
import cz.pecawolf.data.model.toDomain
import cz.pecawolf.domain.model.PhotoFeed
import cz.pecawolf.domain.repository.PhotoRepository

class PhotoRepositoryImpl(
    private val photoApi: PhotoApi,
) : PhotoRepository {
    override suspend fun getPhotoFeed(
        tags: List<String>,
        matchAllTags: Boolean,
    ): Result<PhotoFeed> = runCatching {
        if (tags.isNotEmpty()) {
            photoApi.getPhotos(
                tags = tags.joinToString(","),
                tagMode = if (matchAllTags) {
                    "all"
                } else {
                    "any"
                }
            )
        } else {
            photoApi.getPhotos(
                tags = null,
                tagMode = null,
            )
        }.toDomain()
    }
}

