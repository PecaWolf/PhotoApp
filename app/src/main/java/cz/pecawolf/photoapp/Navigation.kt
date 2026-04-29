package cz.pecawolf.photoapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import cz.pecawolf.presentation.screens.home.HomeRoute

data object Screens {
    const val Home = "home"
}

@Composable
fun NavGraph(navController: NavController) {
    NavHost(navController as NavHostController, startDestination = Screens.Home) {
        composable(Screens.Home) {
            HomeRoute()
        }
    }
}
