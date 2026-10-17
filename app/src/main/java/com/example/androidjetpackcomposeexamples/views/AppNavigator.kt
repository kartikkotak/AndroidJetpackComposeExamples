package com.example.androidjetpackcomposeexamples.views

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.androidjetpackcomposeexamples.data.PhotoRepository
import com.example.androidjetpackcomposeexamples.viewmodel.PhotoViewModel

@Composable
fun AppNavigator(navController: NavHostController, photoRepository: PhotoRepository, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Home,
        modifier = modifier,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { width -> width },
                animationSpec = tween()
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { width -> -width },
                animationSpec = tween()
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { width -> -width },
                animationSpec = tween()
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { width -> width },
                animationSpec = tween()
            )
        },
    ) {
        composable<AppRoute.Home>() {
            HomeScreen(
                homeActions = HomeActions(
                    onPlaygroundClick = { navController.navigate(AppRoute.ComponentPlayground) },
                    onListsClick = { navController.navigate(AppRoute.ListsAndGrids) },
                    onCarouselsClick = { navController.navigate(AppRoute.Carousels) },
                    onAdvancedUiClick = { navController.navigate(AppRoute.AdvancedUi) },
                    onSettingsClick = { navController.navigate(AppRoute.Settings) },
                )
            )
        }
        composable<AppRoute.ComponentPlayground>() {
            ComponentPlaygroundScreen(
                { navController.navigate(AppRoute.Button)},
                { navController.navigate(AppRoute.TextFields)}
            )
        }
        composable<AppRoute.TextFields>() {
            TextFieldsExampleScreen()
        }
        composable<AppRoute.ListsAndGrids> {
            ExampleMenu(listOf(
                "Interactive list" to { navController.navigate(AppRoute.EditableList) },
                "Photo list" to { navController.navigate(AppRoute.PhotoList) },
                "Photo grid" to { navController.navigate(AppRoute.PhotoGrid) },
            ))
        }
        composable<AppRoute.Carousels> {
            ExampleMenu(listOf(
                "Horizontal pager" to { navController.navigate(AppRoute.PhotoPager) },
                "LazyRow carousel" to { navController.navigate(AppRoute.PhotoCarousel) },
            ))
        }
        composable<AppRoute.AdvancedUi> {
            ExampleMenu(listOf("Location and Map" to { navController.navigate(AppRoute.LocationMap) }))
        }
        composable<AppRoute.LocationMap> { LocationMapRoute() }
        composable<AppRoute.EditableList> { EditableListScreen() }
        composable<AppRoute.PhotoList> { PhotoRoute(PhotoLayout.List, photoRepository) }
        composable<AppRoute.PhotoGrid> { PhotoRoute(PhotoLayout.Grid, photoRepository) }
        composable<AppRoute.PhotoPager> { PhotoRoute(PhotoLayout.Pager, photoRepository) }
        composable<AppRoute.PhotoCarousel> { PhotoRoute(PhotoLayout.Carousel, photoRepository) }
        composable<AppRoute.Settings>() {
            SettingsScreen()
        }
        composable<AppRoute.Button>() {
            ButtonExampleScreen()
        }
    }
}

@Composable
private fun PhotoRoute(layout: PhotoLayout, repository: PhotoRepository) {
    val model: PhotoViewModel = viewModel(factory = viewModelFactory {
        initializer { PhotoViewModel(repository) }
    })
    PhotoExamplesScreen(layout, model)
}
