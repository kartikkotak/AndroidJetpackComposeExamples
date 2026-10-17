package com.example.androidjetpackcomposeexamples.views

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.dp

@Composable
fun ExampleMenu(entries: List<Pair<String, () -> Unit>>) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)) {
        items(entries, key = { it.first }) { (title, action) ->
            MenuCardWithChevron(title = title, onClick = action)
        }
    }
}
