package cz.pecawolf.data.repository

import cz.pecawolf.data.api.PhotoApi
import cz.pecawolf.data.model.PhotoItemDto
import cz.pecawolf.data.model.toDomain
import cz.pecawolf.domain.model.PhotoItem
import cz.pecawolf.domain.repository.PhotoRepository

class PhotoRepositoryImpl(
    private val photoApi: PhotoApi,
) : PhotoRepository {
    override suspend fun getPhotos(): Result<List<PhotoItem>> = runCatching {
        photoApi.getPhotos().items.map(PhotoItemDto::toDomain)
    }
}

