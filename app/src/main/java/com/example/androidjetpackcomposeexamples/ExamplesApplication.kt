package com.example.androidjetpackcomposeexamples

import android.app.Application
import com.example.androidjetpackcomposeexamples.data.DefaultPhotoRepository
import com.example.androidjetpackcomposeexamples.data.PicsumPhotoApi
import com.example.androidjetpackcomposeexamples.data.PhotoRepository

class ExamplesApplication : Application() {
    val photoRepository: PhotoRepository by lazy { DefaultPhotoRepository(PicsumPhotoApi()) }
}
