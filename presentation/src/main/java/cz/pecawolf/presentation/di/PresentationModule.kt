package cz.pecawolf.presentation.di

import cz.pecawolf.presentation.viewmodel.ExampleViewModel
import cz.pecawolf.presentation.viewmodel.HomeViewModel
import cz.pecawolf.presentation.viewmodel.FullScreenImageViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::FullScreenImageViewModel)

    viewModelOf(::ExampleViewModel)
}