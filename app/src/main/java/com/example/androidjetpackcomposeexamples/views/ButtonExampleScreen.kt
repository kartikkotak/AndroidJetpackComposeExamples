package com.example.androidjetpackcomposeexamples.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ButtonExampleScreen() {
    var enabled by rememberSaveable { mutableStateOf(true) }
    var clicks by rememberSaveable { mutableIntStateOf(0) }
    var favorite by rememberSaveable { mutableStateOf(false) }
    var segment by rememberSaveable { mutableIntStateOf(0) }
    var selectedDays by rememberSaveable { mutableIntStateOf(0) }
    var loading by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(loading) {
        if (loading) { delay(1500); loading = false }
    }
    ExampleColumn {
        Text("Button styles", style = MaterialTheme.typography.headlineSmall)
        Text("Clicks: $clicks · Favorite: ${if (favorite) "on" else "off"}")
        Row { Text("Enable examples", Modifier.weight(1f)); Switch(enabled, { enabled = it }) }
        Button(onClick = { clicks++ }, enabled = enabled) { Text("Filled") }
        ElevatedButton(onClick = { clicks++ }, enabled = enabled) { Text("Elevated") }
        FilledTonalButton(onClick = { clicks++ }, enabled = enabled) { Text("Filled tonal") }
        OutlinedButton(onClick = { clicks++ }, enabled = enabled) { Text("Outlined") }
        TextButton(onClick = { clicks++ }, enabled = enabled) { Text("Text") }
        Button(onClick = { clicks++ }, enabled = enabled) {
            Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("With an icon")
        }
        Text("Icon buttons", style = MaterialTheme.typography.titleLarge)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(onClick = { clicks++ }, enabled = enabled) { Icon(Icons.Default.Add, "Standard icon button") }
            FilledIconButton(onClick = { clicks++ }, enabled = enabled) { Icon(Icons.Default.Add, "Filled icon button") }
            FilledTonalIconButton(onClick = { clicks++ }, enabled = enabled) { Icon(Icons.Default.Add, "Tonal icon button") }
            OutlinedIconButton(onClick = { clicks++ }, enabled = enabled) { Icon(Icons.Default.Add, "Outlined icon button") }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconToggleButton(favorite, { favorite = it }, enabled = enabled) { Icon(Icons.Default.Favorite, "Toggle favorite") }
            FilledIconToggleButton(favorite, { favorite = it }, enabled = enabled) { Icon(Icons.Default.Favorite, "Filled toggle favorite") }
            FilledTonalIconToggleButton(favorite, { favorite = it }, enabled = enabled) { Icon(Icons.Default.Favorite, "Tonal toggle favorite") }
            OutlinedIconToggleButton(favorite, { favorite = it }, enabled = enabled) { Icon(Icons.Default.Favorite, "Outlined toggle favorite") }
        }
        Text("Floating action buttons", style = MaterialTheme.typography.titleLarge)
        Text("FABs represent a screen's primary action and have no disabled parameter.")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SmallFloatingActionButton(onClick = { clicks++ }) { Icon(Icons.Default.Add, "Small FAB") }
            FloatingActionButton(onClick = { clicks++ }) { Icon(Icons.Default.Add, "Standard FAB") }
            LargeFloatingActionButton(onClick = { clicks++ }) { Icon(Icons.Default.Add, "Large FAB") }
        }
        ExtendedFloatingActionButton(onClick = { clicks++ }, icon = { Icon(Icons.Default.Add, null) }, text = { Text("Create item") })
        Text("Segmented buttons", style = MaterialTheme.typography.titleLarge)
        SingleChoiceSegmentedButtonRow {
            listOf("Day", "Week", "Month").forEachIndexed { index, label ->
                SegmentedButton(
                    selected = segment == index,
                    onClick = { segment = index },
                    enabled = enabled,
                    shape = SegmentedButtonDefaults.itemShape(index, 3),
                ) { Text(label) }
            }
        }
        Text("Selected: ${listOf("Day", "Week", "Month")[segment]}")
        Text("Multiple selection", style = MaterialTheme.typography.titleMedium)
        MultiChoiceSegmentedButtonRow {
            listOf("Mon", "Tue", "Wed").forEachIndexed { index, label ->
                SegmentedButton(
                    checked = selectedDays and (1 shl index) != 0,
                    onCheckedChange = { selectedDays = selectedDays xor (1 shl index) },
                    enabled = enabled,
                    shape = SegmentedButtonDefaults.itemShape(index, 3),
                ) { Text(label) }
            }
        }
        Text("Loading action", style = MaterialTheme.typography.titleLarge)
        Button(onClick = { loading = true }, enabled = enabled && !loading) {
            if (loading) {
                CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
            }
            Text(if (loading) "Saving…" else "Simulate save")
        }
    }
}

@Composable
internal fun ExampleColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content,
    )
}
