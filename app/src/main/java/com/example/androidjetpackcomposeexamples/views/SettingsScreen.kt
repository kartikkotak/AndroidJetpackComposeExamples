package com.example.androidjetpackcomposeexamples.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.androidjetpackcomposeexamples.ExamplesApplication
import com.example.androidjetpackcomposeexamples.data.*
import com.example.androidjetpackcomposeexamples.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen() {
    val repository = (LocalContext.current.applicationContext as ExamplesApplication).settingsRepository
    val model: SettingsViewModel = viewModel(factory = viewModelFactory {
        initializer { SettingsViewModel(repository) }
    })
    val settings by model.settings.collectAsStateWithLifecycle()
    val error by model.error.collectAsStateWithLifecycle()
    val current = settings
    if (current == null) {
        CircularProgressIndicator(Modifier.padding(24.dp))
        return
    }
    SettingsContent(current, error, model::appearance, model::palette, model::dynamicColors, model::font, model::shape, { model.reset() })
}

@Composable
internal fun SettingsContent(
    settings: AppSettings,
    error: String?,
    onAppearance: (Appearance) -> Unit,
    onPalette: (AppPalette) -> Unit,
    onDynamicColors: (Boolean) -> Unit,
    onFont: (AppFont) -> Unit,
    onShape: (AppShape) -> Unit,
    onReset: () -> Unit,
) {
    ExampleColumn {
        Text("Appearance", style = MaterialTheme.typography.headlineSmall)
        ChoiceGroup(Appearance.entries, settings.appearance, { it.label }, onSelect = onAppearance)
        HorizontalDivider()
        Text("Colors", style = MaterialTheme.typography.titleLarge)
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                Text("Dynamic colors", style = MaterialTheme.typography.titleMedium)
                Text("Use colors from your wallpaper")
            }
            Switch(settings.dynamicColors, onDynamicColors, modifier = Modifier.semantics { contentDescription = "Dynamic colors" })
        }
        Text(if (settings.dynamicColors) "Wallpaper colors override the saved palette. Turn off Dynamic colors to choose a palette." else "Choose an app palette")
        ChoiceGroup(AppPalette.entries, settings.palette, { it.label }, enabled = !settings.dynamicColors, onSelect = onPalette)
        HorizontalDivider()
        Text("Typography", style = MaterialTheme.typography.titleLarge)
        ChoiceGroup(AppFont.entries, settings.font, { it.label }, onSelect = onFont)
        Text("Shapes", style = MaterialTheme.typography.titleLarge)
        ChoiceGroup(AppShape.entries, settings.shape, { it.label }, onSelect = onShape)
        HorizontalDivider()
        Text("Live preview", style = MaterialTheme.typography.titleLarge)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Your app, your style", style = MaterialTheme.typography.headlineSmall)
                Text("Colors, type, and corners update across the app.", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = {}, shape = MaterialTheme.shapes.small) { Text("Preview button") }
                OutlinedTextField(value = "Sample text", onValueChange = {}, readOnly = true, label = { Text("Preview field") }, modifier = Modifier.fillMaxWidth())
            }
        }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        OutlinedButton(onClick = onReset, shape = MaterialTheme.shapes.small) { Text("Reset to defaults") }
    }
}

@Composable
private fun <T> ChoiceGroup(
    options: List<T>, selected: T, label: (T) -> String,
    enabled: Boolean = true, onSelect: (T) -> Unit,
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            FilterChip(selected = option == selected, onClick = { onSelect(option) }, enabled = enabled, label = { Text(label(option)) })
        }
    }
}
