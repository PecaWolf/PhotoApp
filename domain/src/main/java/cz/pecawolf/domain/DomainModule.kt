package cz.pecawolf.domain

import cz.pecawolf.domain.usecase.ExampleUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::ExampleUseCase)
}