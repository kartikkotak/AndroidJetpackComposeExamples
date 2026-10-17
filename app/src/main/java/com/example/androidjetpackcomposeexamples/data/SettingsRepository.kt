package com.example.androidjetpackcomposeexamples.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

enum class Appearance(val label: String) { System("System default"), Light("Light"), Dark("Dark") }
enum class AppPalette(val label: String) { Default("Default"), Ocean("Ocean"), Forest("Forest"), Purple("Purple") }
enum class AppFont(val label: String) { Default("Default"), Serif("Serif") }
enum class AppShape(val label: String) { Rounded("Rounded"), SlightlyRounded("Slightly rounded"), Square("Square") }
data class AppSettings(
    val appearance: Appearance = Appearance.System,
    val palette: AppPalette = AppPalette.Default,
    val dynamicColors: Boolean = true,
    val font: AppFont = AppFont.Default,
    val shape: AppShape = AppShape.Rounded,
)

interface SettingsRepository {
    val settings: Flow<AppSettings>
    suspend fun update(transform: (AppSettings) -> AppSettings)
    suspend fun reset()
}

val Context.appearanceDataStore: DataStore<Preferences> by preferencesDataStore(name = "appearance")

class DataStoreSettingsRepository(private val store: DataStore<Preferences>) : SettingsRepository {
    override val settings = store.data.catch {
        if (it is IOException) emit(emptyPreferences()) else throw it
    }.map(::decode)

    override suspend fun update(transform: (AppSettings) -> AppSettings) {
        store.edit { preferences ->
            val settings = transform(decode(preferences))
            preferences[appearance] = settings.appearance.name
            preferences[palette] = settings.palette.name
            preferences[dynamicColors] = settings.dynamicColors
            preferences[font] = settings.font.name
            preferences[shape] = settings.shape.name
        }
    }

    override suspend fun reset() { update { AppSettings() } }

    private fun decode(preferences: Preferences) = AppSettings(
        appearance = enumValue(preferences[appearance], Appearance.System),
        palette = enumValue(preferences[palette], AppPalette.Default),
        dynamicColors = preferences[dynamicColors] ?: true,
        font = enumValue(preferences[font], AppFont.Default),
        shape = enumValue(preferences[shape], AppShape.Rounded),
    )
    private inline fun <reified T : Enum<T>> enumValue(value: String?, fallback: T): T =
        enumValues<T>().firstOrNull { it.name == value } ?: fallback

    private companion object {
        val appearance = stringPreferencesKey("appearance")
        val palette = stringPreferencesKey("palette")
        val dynamicColors = booleanPreferencesKey("dynamic_colors")
        val font = stringPreferencesKey("font")
        val shape = stringPreferencesKey("shape")
    }
}
