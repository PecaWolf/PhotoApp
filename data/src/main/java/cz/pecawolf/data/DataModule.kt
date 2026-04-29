package cz.pecawolf.data

import cz.pecawolf.data.repository.ExampleRepositoryImpl
import cz.pecawolf.domain.repository.ExampleRepository
import cz.pecawolf.domain.usecase.ExampleUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    singleOf(::ExampleRepositoryImpl) bind ExampleRepository::class
}