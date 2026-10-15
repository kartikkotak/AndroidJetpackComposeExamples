package com.example.androidjetpackcomposeexamples.views

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController

@Composable
fun AppNavigator(navController: NavHostController, modifier: Modifier = Modifier) {
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
                onPlaygroundClick = { navController.navigate(AppRoute.ComponentPlayground) },
                onSettingsClick = { navController.navigate(AppRoute.Settings) },
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
        composable<AppRoute.Settings>() {
            SettingsScreen()
        }
        composable<AppRoute.Button>() {
            ButtonExampleScreen()
        }
    }
}