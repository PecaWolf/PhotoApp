package cz.pecawolf.domain.repository

import cz.pecawolf.domain.model.PhotoFeed

interface PhotoRepository {
    suspend fun getPhotoFeed(): Result<PhotoFeed>
}
