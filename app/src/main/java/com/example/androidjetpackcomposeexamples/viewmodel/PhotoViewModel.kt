package com.example.androidjetpackcomposeexamples.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidjetpackcomposeexamples.data.DefaultPhotoRepository
import com.example.androidjetpackcomposeexamples.data.Photo
import com.example.androidjetpackcomposeexamples.data.PhotoRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PhotoUiState(
    val photos: List<Photo> = emptyList(),
    val selectedIds: Set<String> = emptySet(),
    val loading: Boolean = false,
    val error: String? = null,
    val endReached: Boolean = false,
)

class PhotoViewModel(private val repository: PhotoRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(PhotoUiState())
    val state = mutableState.asStateFlow()
    private var nextPage = 1
    private var retryRefresh = false

    init { load() }

    fun loadMore() {
        if (state.value.error == null) load()
    }

    fun retry() = load(refresh = retryRefresh)
    fun refresh() = load(refresh = true)

    private fun load(refresh: Boolean = false) {
        if (state.value.loading || (!refresh && state.value.endReached)) return
        retryRefresh = refresh
        mutableState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                val page = if (refresh) 1 else nextPage
                val photos = repository.getPage(page, refresh)
                mutableState.update {
                    it.copy(
                        photos = ((if (refresh) emptyList() else it.photos) + photos).distinctBy(Photo::id),
                        selectedIds = if (refresh) emptySet() else it.selectedIds,
                        loading = false,
                        endReached = photos.size < DefaultPhotoRepository.PAGE_SIZE,
                    )
                }
                nextPage = page + 1
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.update { it.copy(loading = false, error = "Could not load photos. Check your connection and retry.") }
            }
        }
    }

    fun toggleSelection(id: String) {
        mutableState.update {
            if (it.photos.none { photo -> photo.id == id }) it
            else it.copy(selectedIds = if (id in it.selectedIds) it.selectedIds - id else it.selectedIds + id)
        }
    }

    fun clearSelection() { mutableState.update { it.copy(selectedIds = emptySet()) } }
}
