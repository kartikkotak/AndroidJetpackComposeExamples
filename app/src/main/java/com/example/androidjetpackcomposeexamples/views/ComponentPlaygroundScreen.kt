package com.example.androidjetpackcomposeexamples.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ComponentPlaygroundScreen(
    onButtonExampleClick: () -> Unit,
    onTextFieldsExampleClick: () -> Unit,
    ) {

    val examplesList = listOf<Pair<String, () -> Unit>>(
        "Buttons" to onButtonExampleClick,
        "Text fields" to onTextFieldsExampleClick,
    )
    Column (
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        examplesList.forEach { (title, screen) ->
            MenuCardWithChevron(
                title = title,
                onClick = screen,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}