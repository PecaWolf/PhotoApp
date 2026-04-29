package cz.pecawolf.data.repository

import cz.pecawolf.domain.repository.ExampleRepository

class ExampleRepositoryImpl : ExampleRepository {
    override fun getExample() = Result.success(Unit)
}