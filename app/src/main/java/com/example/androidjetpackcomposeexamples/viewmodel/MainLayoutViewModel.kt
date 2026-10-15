package com.example.androidjetpackcomposeexamples.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


class MainLayoutViewModel: ViewModel() {
    var appBarTitle by mutableStateOf("Compose Examples")
        private set
    fun updateTitle(newTitle: String) {
        if(newTitle.isNotBlank()) appBarTitle = newTitle
    }
}