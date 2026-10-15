package com.example.androidjetpackcomposeexamples.views

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable data object Home : AppRoute
    @Serializable data object ComponentPlayground : AppRoute
    @Serializable data object Button : AppRoute
    @Serializable data object TextFields : AppRoute
    @Serializable data object Settings : AppRoute
}
