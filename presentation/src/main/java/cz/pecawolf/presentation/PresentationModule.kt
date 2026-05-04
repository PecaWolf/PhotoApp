package cz.pecawolf.presentation

import cz.pecawolf.presentation.screens.example.ExampleViewModel
import cz.pecawolf.presentation.screens.home.HomeViewModel
import cz.pecawolf.presentation.screens.image.FullScreenImageViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::FullScreenImageViewModel)

    viewModelOf(::ExampleViewModel)
}