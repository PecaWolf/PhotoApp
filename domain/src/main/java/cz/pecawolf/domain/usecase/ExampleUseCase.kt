package cz.pecawolf.domain.usecase

import cz.pecawolf.domain.repository.ExampleRepository

class ExampleUseCase(
    private val repository: ExampleRepository,
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.getExample()
    }
}