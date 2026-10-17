package com.example.androidjetpackcomposeexamples.views

import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable data object Home : AppRoute
    @Serializable data object ComponentPlayground : AppRoute
    @Serializable data object Button : AppRoute
    @Serializable data object TextFields : AppRoute
    @Serializable data object ListsAndGrids : AppRoute
    @Serializable data object EditableList : AppRoute
    @Serializable data object PhotoList : AppRoute
    @Serializable data object PhotoGrid : AppRoute
    @Serializable data object Carousels : AppRoute
    @Serializable data object PhotoPager : AppRoute
    @Serializable data object PhotoCarousel : AppRoute
    @Serializable data object AdvancedUi : AppRoute
    @Serializable data object LocationMap : AppRoute
    @Serializable data object Settings : AppRoute
}
