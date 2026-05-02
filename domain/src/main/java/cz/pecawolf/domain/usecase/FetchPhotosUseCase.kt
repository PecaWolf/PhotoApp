package cz.pecawolf.domain.usecase

import cz.pecawolf.domain.model.PhotoItem
import cz.pecawolf.domain.repository.PhotoRepository

class FetchPhotosUseCase(
    private val repository: PhotoRepository,
) {
    suspend operator fun invoke(): Result<List<PhotoItem>> = repository.getPhotos()
}