package cz.pecawolf.photoapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cz.pecawolf.presentation.screens.home.HomeRoute
import cz.pecawolf.presentation.screens.image.FullScreenImageRoute

data object Screens {
    const val Home = "home"
    const val FullScreenImage = "image"
}

@Composable
fun NavGraph(navController: NavController) {
    NavHost(navController as NavHostController, startDestination = Screens.Home) {
        composable(Screens.Home) {
            HomeRoute(
                onNavigateToItemFullScreen = { navController.navigate(Screens.FullScreenImage + "/${it}") },
            )
        }
        composable(Screens.FullScreenImage + "/{imageUrl}") {
            FullScreenImageRoute(
                onNavigateBack = navController::navigateUp
            )
        }
    }
}
