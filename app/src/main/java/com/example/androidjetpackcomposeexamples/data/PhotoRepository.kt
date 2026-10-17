package com.example.androidjetpackcomposeexamples.data

import java.net.HttpURLConnection
import java.net.URI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// UI models do not depend on the service's response format.
data class Photo(val id: String, val author: String) {
    val imageUrl: String get() = "https://picsum.photos/id/$id/800/600"
}

interface PhotoRepository {
    suspend fun getPage(page: Int, refresh: Boolean = false): List<Photo>
}

fun interface PhotoApi {
    suspend fun getPhotos(page: Int, limit: Int): List<Photo>
}

class PicsumPhotoApi : PhotoApi {
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getPhotos(page: Int, limit: Int): List<Photo> = withContext(Dispatchers.IO) {
        val connection = URI("https://picsum.photos/v2/list?page=$page&limit=$limit")
            .toURL().openConnection() as HttpURLConnection
        try {
            connection.connectTimeout = 15_000
            connection.readTimeout = 15_000
            check(connection.responseCode in 200..299) { "Photo service is unavailable" }
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            ensureActive()
            json.decodeFromString<List<PhotoResponse>>(body).map { Photo(it.id, it.author) }
        } finally {
            connection.disconnect()
        }
    }

    @Serializable
    private data class PhotoResponse(val id: String, val author: String)
}

class DefaultPhotoRepository(private val api: PhotoApi) : PhotoRepository {
    private val mutex = Mutex()
    // Bound the in-memory page cache. Coil owns the separate image cache.
    private val pages = LinkedHashMap<Int, List<Photo>>()

    override suspend fun getPage(page: Int, refresh: Boolean): List<Photo> = mutex.withLock {
        require(page > 0)
        if (refresh) pages.clear()
        pages[page] ?: api.getPhotos(page, PAGE_SIZE).also {
            if (pages.size >= 10) pages.remove(pages.keys.first())
            pages[page] = it
        }
    }

    companion object { const val PAGE_SIZE = 20 }
}
