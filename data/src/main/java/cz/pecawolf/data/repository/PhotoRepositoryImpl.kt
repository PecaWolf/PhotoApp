package cz.pecawolf.data.repository

import cz.pecawolf.data.api.PhotoApi
import cz.pecawolf.data.model.toDomain
import cz.pecawolf.domain.model.PhotoFeed
import cz.pecawolf.domain.repository.PhotoRepository

class PhotoRepositoryImpl(
    private val photoApi: PhotoApi,
) : PhotoRepository {
    override suspend fun getPhotoFeed(): Result<PhotoFeed> = runCatching {
        photoApi.getPhotos().toDomain()
    }
}

