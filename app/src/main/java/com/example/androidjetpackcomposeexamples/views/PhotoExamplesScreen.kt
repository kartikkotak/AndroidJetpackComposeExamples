package com.example.androidjetpackcomposeexamples.views

import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.gestures.snapping.snapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.androidjetpackcomposeexamples.data.Photo
import com.example.androidjetpackcomposeexamples.viewmodel.PhotoViewModel
import kotlinx.coroutines.flow.distinctUntilChanged

enum class PhotoLayout { List, Grid, Pager, Carousel }

@Composable
fun PhotoExamplesScreen(layout: PhotoLayout, viewModel: PhotoViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()
    val pagerState = rememberPagerState { state.photos.size }
    LaunchedEffect(layout, state.photos.size) {
        if (state.photos.isEmpty()) return@LaunchedEffect
        snapshotFlow {
            when (layout) {
                PhotoLayout.Grid -> (gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1) >= state.photos.size - 4
                PhotoLayout.Pager -> pagerState.currentPage >= state.photos.size - 3
                else -> (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1) >= state.photos.size - 4
            }
        }.distinctUntilChanged().collect { nearEnd -> if (nearEnd) viewModel.loadMore() }
    }
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("${state.photos.size} photos", Modifier.weight(1f))
            TextButton(onClick = viewModel::refresh, enabled = !state.loading) { Text("Refresh") }
        }
        if (layout == PhotoLayout.List || layout == PhotoLayout.Grid) {
            Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("${state.selectedIds.size} selected", Modifier.weight(1f))
                TextButton(onClick = viewModel::clearSelection, enabled = state.selectedIds.isNotEmpty()) { Text("Clear selection") }
            }
        }
        if (state.error != null) {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Text(state.error!!, color = MaterialTheme.colorScheme.error)
                Button(onClick = viewModel::retry) { Text("Retry") }
            }
        }
        if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        if (state.photos.isEmpty() && !state.loading && state.error == null) {
            Text("No photos available. Try refreshing.", Modifier.padding(16.dp))
        }
        when (layout) {
            PhotoLayout.List -> LazyColumn(
                state = listState, modifier = Modifier.weight(1f), contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                items(state.photos, key = { it.id }) { photo ->
                    PhotoCard(photo, selected = photo.id in state.selectedIds, onSelect = { viewModel.toggleSelection(photo.id) })
                }
            }
            PhotoLayout.Grid -> LazyVerticalGrid(
                columns = GridCells.Adaptive(160.dp), state = gridState, modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.photos, key = { it.id }) { photo ->
                    PhotoCard(photo, selected = photo.id in state.selectedIds, onSelect = { viewModel.toggleSelection(photo.id) })
                }
            }
            PhotoLayout.Pager -> if (state.photos.isNotEmpty()) {
                Text("Page ${pagerState.currentPage + 1} of ${state.photos.size}", Modifier.padding(horizontal = 16.dp))
                HorizontalPager(
                    state = pagerState, key = { state.photos[it].id }, modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 24.dp), pageSpacing = 12.dp,
                ) { page -> PhotoCard(state.photos[page]) }
            }
            PhotoLayout.Carousel -> {
                Text("Swipe through photos · snaps to each card", Modifier.padding(horizontal = 16.dp))
                BoxWithConstraints(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    val cardWidth = (maxWidth * 0.82f).coerceAtMost(400.dp)
                    LazyRow(
                        state = listState, flingBehavior = rememberSnapFlingBehavior(listState),
                        contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(state.photos, key = { it.id }) { photo -> PhotoCard(photo, Modifier.width(cardWidth)) }
                    }
                }
            }
        }
        if (state.endReached && state.photos.isNotEmpty()) Text("All photos loaded", Modifier.padding(horizontal = 16.dp))
    }
}

@Composable
private fun PhotoCard(photo: Photo, modifier: Modifier = Modifier, selected: Boolean = false, onSelect: (() -> Unit)? = null) {
    Card(modifier = modifier.fillMaxWidth()) {
        Box {
            var failed by remember(photo.imageUrl) { mutableStateOf(false) }
            var loading by remember(photo.imageUrl) { mutableStateOf(true) }
            AsyncImage(
                model = photo.imageUrl, contentDescription = "Photo ${photo.id} by ${photo.author}",
                modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f), contentScale = ContentScale.Crop,
                onLoading = { loading = true; failed = false },
                onSuccess = { loading = false; failed = false },
                onError = { loading = false; failed = true },
            )
            if (loading) CircularProgressIndicator(Modifier.align(Alignment.Center).size(24.dp))
            if (failed) Text("Image unavailable", Modifier.align(Alignment.Center))
        }
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(photo.author, style = MaterialTheme.typography.titleMedium)
                Text("Photo #${photo.id}", style = MaterialTheme.typography.bodySmall)
            }
            if (onSelect != null) Checkbox(selected, { onSelect() })
        }
    }
}
