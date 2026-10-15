package com.example.androidjetpackcomposeexamples.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.androidjetpackcomposeexamples.R

@Composable
fun SettingsScreen(updateTitle: (String) -> Unit) {
    val title = stringResource(R.string.settings_screen_title)
    LaunchedEffect(Unit) {
        updateTitle(title)
    }
    Text(
        "Welcome to the settings screen",
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        )
}