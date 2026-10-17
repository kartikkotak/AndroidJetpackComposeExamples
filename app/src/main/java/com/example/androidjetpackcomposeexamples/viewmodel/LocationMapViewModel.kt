package com.example.androidjetpackcomposeexamples.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidjetpackcomposeexamples.data.LocationFix
import com.example.androidjetpackcomposeexamples.data.LocationProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LocationMapState(
    val location: LocationFix? = null,
    val loading: Boolean = false,
    val error: String? = null,
)

class LocationMapViewModel(private val provider: LocationProvider) : ViewModel() {
    private val mutableState = MutableStateFlow(LocationMapState())
    val state = mutableState.asStateFlow()
    private var request: Job? = null
    private var generation = 0

    fun locate() {
        if (state.value.loading) return
        val currentGeneration = ++generation
        mutableState.update { it.copy(loading = true, error = null) }
        request = viewModelScope.launch {
            try {
                val fix = provider.currentLocation()
                mutableState.value = LocationMapState(
                    location = fix,
                    error = if (fix == null) "No location fix yet. Try outdoors or set an emulator location, then retry." else null,
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: SecurityException) {
                mutableState.value = LocationMapState(error = "Allow location access, then try again.")
            } catch (error: Exception) {
                mutableState.update { it.copy(loading = false, error = error.message ?: "Could not get your location. Try again.") }
            } finally {
                if (generation == currentGeneration) mutableState.update { it.copy(loading = false) }
            }
        }
    }

    // A one-shot request must not continue after the screen leaves the foreground.
    fun stop() {
        generation++
        request?.cancel()
        request = null
        mutableState.update { it.copy(loading = false) }
    }

    fun permissionRevoked() {
        stop()
        mutableState.value = LocationMapState()
    }
}
