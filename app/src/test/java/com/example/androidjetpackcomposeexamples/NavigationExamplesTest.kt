package com.example.androidjetpackcomposeexamples

import androidx.lifecycle.SavedStateHandle
import com.example.androidjetpackcomposeexamples.data.*
import com.example.androidjetpackcomposeexamples.viewmodel.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NavigationExamplesTest {
    private val dispatcher = StandardTestDispatcher()
    @Before fun setup() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    @Test fun repositorySharesPagesAndRefreshInvalidatesCache() = runTest(dispatcher) {
        var calls = 0
        val repository = DefaultPhotoRepository(PhotoApi { page, _ ->
            calls++
            listOf(Photo("$page", "Author"))
        })
        repository.getPage(1)
        repository.getPage(1)
        assertEquals(1, calls)
        repository.getPage(2)
        repository.getPage(1, refresh = true)
        repository.getPage(2)
        assertEquals(4, calls)
    }

    @Test fun paginationGuardsConcurrentCallsAndRetriesSamePage() = runTest(dispatcher) {
        val calls = mutableListOf<Int>()
        var fail = false
        val repository = object : PhotoRepository {
            override suspend fun getPage(page: Int, refresh: Boolean): List<Photo> {
                calls += page
                if (fail) error("Offline")
                return if (page == 1) (1..20).map { Photo("$it", "Author") } else emptyList()
            }
        }
        val model = PhotoViewModel(repository)
        model.loadMore()
        advanceUntilIdle()
        assertEquals(listOf(1), calls)
        model.toggleSelection("1")
        fail = true
        model.loadMore()
        advanceUntilIdle()
        assertNotNull(model.state.value.error)
        model.loadMore()
        advanceUntilIdle()
        assertEquals(listOf(1, 2), calls)
        fail = false
        model.retry()
        advanceUntilIdle()
        assertEquals(listOf(1, 2, 2), calls)
        assertTrue(model.state.value.endReached)
        assertEquals(setOf("1"), model.state.value.selectedIds)
        model.refresh()
        advanceUntilIdle()
        assertTrue(model.state.value.selectedIds.isEmpty())
    }

    @Test fun failedRefreshRetriesRefreshInsteadOfAppending() = runTest(dispatcher) {
        var fail = false
        val requests = mutableListOf<Pair<Int, Boolean>>()
        val model = PhotoViewModel(object : PhotoRepository {
            override suspend fun getPage(page: Int, refresh: Boolean): List<Photo> {
                requests += page to refresh
                if (fail) error("Offline")
                return (1..20).map { Photo("$it", "Author") }
            }
        })
        advanceUntilIdle()
        fail = true
        model.refresh()
        advanceUntilIdle()
        fail = false
        model.retry()
        advanceUntilIdle()
        assertEquals(listOf(1 to false, 1 to true, 1 to true), requests)
        assertEquals(20, model.state.value.photos.size)
    }

    @Test fun editableListDeletesSelectionAndRestoresSavedState() {
        val saved = SavedStateHandle()
        val model = EditableListViewModel(saved)
        model.add("  New item  ")
        model.add(" ")
        val added = model.state.value.items.last()
        assertEquals("New item", added.title)
        model.toggleSelection(added.id)
        model.toggleSelection(1)
        val restored = EditableListViewModel(saved)
        assertEquals(model.state.value, restored.state.value)
        restored.deleteSelected()
        assertEquals(4, restored.state.value.items.size)
        assertTrue(restored.state.value.selectedIds.isEmpty())
        assertFalse(restored.state.value.items.any { it.id == added.id || it.id == 1L })
    }
}
