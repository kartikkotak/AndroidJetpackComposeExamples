package com.example.androidjetpackcomposeexamples.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidjetpackcomposeexamples.data.*
import java.io.IOException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    val settings = repository.settings.stateIn(viewModelScope, SharingStarted.Eagerly, null)
    private val mutableError = MutableStateFlow<String?>(null)
    val error = mutableError.asStateFlow()

    fun appearance(value: Appearance) = save { it.copy(appearance = value) }
    fun palette(value: AppPalette) = save { it.copy(palette = value) }
    fun dynamicColors(value: Boolean) = save { it.copy(dynamicColors = value) }
    fun font(value: AppFont) = save { it.copy(font = value) }
    fun shape(value: AppShape) = save { it.copy(shape = value) }
    fun reset() = save { AppSettings() }

    private fun save(transform: (AppSettings) -> AppSettings) {
        viewModelScope.launch {
            try {
                repository.update(transform)
                mutableError.value = null
            } catch (_: IOException) {
                mutableError.value = "Could not save settings. Please try again."
            }
        }
    }
}
