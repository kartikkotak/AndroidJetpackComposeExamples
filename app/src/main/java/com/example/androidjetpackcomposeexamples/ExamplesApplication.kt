package com.example.androidjetpackcomposeexamples

import android.app.Application
import com.example.androidjetpackcomposeexamples.data.DataStoreSettingsRepository
import com.example.androidjetpackcomposeexamples.data.SettingsRepository
import com.example.androidjetpackcomposeexamples.data.appearanceDataStore
import com.example.androidjetpackcomposeexamples.data.DefaultPhotoRepository
import com.example.androidjetpackcomposeexamples.data.PicsumPhotoApi
import com.example.androidjetpackcomposeexamples.data.PhotoRepository

class ExamplesApplication : Application() {
    val settingsRepository: SettingsRepository by lazy { DataStoreSettingsRepository(appearanceDataStore) }
    val photoRepository: PhotoRepository by lazy { DefaultPhotoRepository(PicsumPhotoApi()) }
}
