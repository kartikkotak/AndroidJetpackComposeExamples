package com.example.androidjetpackcomposeexamples.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class EditableItem(val id: Long, val title: String)
data class EditableListState(val items: List<EditableItem>, val selectedIds: Set<Long> = emptySet())

class EditableListViewModel(private val savedState: SavedStateHandle) : ViewModel() {
    private var nextId: Long = savedState["nextId"] ?: 6L
    private val restoredIds: LongArray = savedState["ids"] ?: longArrayOf(1, 2, 3, 4, 5)
    private val restoredTitles: ArrayList<String> = savedState["titles"] ?: ArrayList((1..5).map { "Item $it" })
    private val mutableState = MutableStateFlow(EditableListState(
        restoredIds.zip(restoredTitles).map { (id, title) -> EditableItem(id, title) },
        (savedState.get<LongArray>("selection") ?: longArrayOf()).toSet(),
    ))
    val state = mutableState.asStateFlow()

    fun add(title: String) {
        if (title.isBlank()) return
        change { it.copy(items = it.items + EditableItem(nextId++, title.trim())) }
    }
    fun remove(id: Long) = change {
        it.copy(items = it.items.filterNot { item -> item.id == id }, selectedIds = it.selectedIds - id)
    }
    fun toggleSelection(id: Long) = change {
        if (it.items.none { item -> item.id == id }) it
        else it.copy(selectedIds = if (id in it.selectedIds) it.selectedIds - id else it.selectedIds + id)
    }
    fun clearSelection() = change { it.copy(selectedIds = emptySet()) }
    fun deleteSelected() = change {
        it.copy(items = it.items.filterNot { item -> item.id in it.selectedIds }, selectedIds = emptySet())
    }
    private fun change(transform: (EditableListState) -> EditableListState) {
        mutableState.update(transform)
        savedState["nextId"] = nextId
        savedState["ids"] = state.value.items.map { it.id }.toLongArray()
        savedState["titles"] = ArrayList(state.value.items.map { it.title })
        savedState["selection"] = state.value.selectedIds.toLongArray()
    }
}
