package com.example.androidjetpackcomposeexamples.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    Text(
        "Welcome to the settings screen",
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        )
}