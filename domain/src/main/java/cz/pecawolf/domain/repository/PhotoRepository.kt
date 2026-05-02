package cz.pecawolf.domain.repository

import cz.pecawolf.domain.model.PhotoItem

interface PhotoRepository {
    suspend fun getPhotos(): Result<List<PhotoItem>>
}
