package cz.pecawolf.domain.usecase

import cz.pecawolf.domain.model.PhotoFeed
import cz.pecawolf.domain.model.PhotoItem
import cz.pecawolf.domain.repository.PhotoRepository

class FetchPhotoFeedUseCase(
    private val repository: PhotoRepository,
) {
    suspend operator fun invoke(): Result<PhotoFeed> = repository.getPhotoFeed()
}