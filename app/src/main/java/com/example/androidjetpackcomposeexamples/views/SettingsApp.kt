package com.example.androidjetpackcomposeexamples.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.androidjetpackcomposeexamples.ExamplesApplication
import com.example.androidjetpackcomposeexamples.ui.theme.AndroidJetpackComposeExamplesTheme
import com.example.androidjetpackcomposeexamples.viewmodel.SettingsViewModel

@Composable
fun SettingsApp() {
    val app = LocalContext.current.applicationContext as ExamplesApplication
    val model: SettingsViewModel = viewModel(factory = viewModelFactory {
        initializer { SettingsViewModel(app.settingsRepository) }
    })
    val settings by model.settings.collectAsStateWithLifecycle()
    val current = settings
    AndroidJetpackComposeExamplesTheme(settings = current ?: com.example.androidjetpackcomposeexamples.data.AppSettings()) {
        if (current == null) {
            Surface(Modifier.fillMaxSize()) {
                Box(contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            }
        } else {
            ExamplesApp()
        }
    }
}
