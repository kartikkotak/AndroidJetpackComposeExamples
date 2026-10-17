package com.example.androidjetpackcomposeexamples.views

import androidx.compose.runtime.Composable

data class HomeActions(
    val onPlaygroundClick: () -> Unit,
    val onSettingsClick: () -> Unit,
    val onListsClick: () -> Unit,
    val onCarouselsClick: () -> Unit,
    val onAdvancedUiClick: () -> Unit,
)
@Composable
fun HomeScreen(
    homeActions: HomeActions,
) {
    ExampleMenu(listOf(
        "Components Playground" to homeActions.onPlaygroundClick,
        "Lists and Grids" to homeActions.onListsClick,
        "Carousels" to homeActions.onCarouselsClick,
        "Advanced UI" to homeActions.onAdvancedUiClick,
        "Settings" to homeActions.onSettingsClick,
    ))
}
