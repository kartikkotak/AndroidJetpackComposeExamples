package com.example.androidjetpackcomposeexamples.views

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class AppScreens {
    Home,
    ComponentPlayground,
    Button,
    TextFields,
    LoginAndSignupForms,
    PhotoGallery,
    Settings,
}

@Composable
fun AppNavigator(modifier: Modifier = Modifier, updateTitle: (String) -> Unit) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreens.Home.name,
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
        composable(AppScreens.Home.name) {
            HomeScreen(navController, updateTitle)
        }
        composable(AppScreens.ComponentPlayground.name) {
            ComponentPlaygroundScreen(navController, updateTitle)
        }
        composable(AppScreens.Settings.name) {
            SettingsScreen(updateTitle)
        }
    }
}