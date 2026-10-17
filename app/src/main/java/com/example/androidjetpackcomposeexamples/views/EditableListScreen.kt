package com.example.androidjetpackcomposeexamples.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androidjetpackcomposeexamples.viewmodel.EditableListViewModel

@Composable
fun EditableListScreen(viewModel: EditableListViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var draft by rememberSaveable { mutableStateOf("") }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Add, select, and remove items", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(draft, { draft = it }, label = { Text("New item") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Button(onClick = { viewModel.add(draft); draft = "" }, enabled = draft.isNotBlank()) { Text("Add item") }
        Text("${state.items.size} items · ${state.selectedIds.size} selected")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = viewModel::deleteSelected, enabled = state.selectedIds.isNotEmpty()) { Text("Delete selected") }
            TextButton(onClick = viewModel::clearSelection, enabled = state.selectedIds.isNotEmpty()) { Text("Clear selection") }
        }
        if (state.items.isEmpty()) Text("Your list is empty. Add an item above.")
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.items, key = { it.id }) { item ->
                Card(onClick = { viewModel.toggleSelection(item.id) }) {
                    Row(Modifier.fillMaxWidth().padding(8.dp)) {
                        Checkbox(item.id in state.selectedIds, { viewModel.toggleSelection(item.id) })
                        Text(item.title, Modifier.weight(1f).padding(12.dp))
                        IconButton(onClick = { viewModel.remove(item.id) }) { Icon(Icons.Default.Delete, "Remove ${item.title}") }
                    }
                }
            }
        }
    }
}
