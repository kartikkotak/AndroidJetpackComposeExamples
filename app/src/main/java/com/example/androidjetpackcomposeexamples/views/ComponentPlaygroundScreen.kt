package com.example.androidjetpackcomposeexamples.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ComponentPlaygroundScreen(navController: NavController, updateTitle: (String) -> Unit) {

    LaunchedEffect(Unit) {
        updateTitle("Component Playground")
    }

    val examplesList = listOf<Pair<String, AppScreens>>(
        "Buttons" to AppScreens.Button,
        "Text fields" to AppScreens.TextFields,
    )
    Column (
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        examplesList.forEach { (title, screen) ->
            MenuCardWithChevron(
                title = title,
            ) {
                navController.navigate(screen.name)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}