package com.example.androidjetpackcomposeexamples

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.androidjetpackcomposeexamples.data.*
import com.example.androidjetpackcomposeexamples.ui.theme.useDarkTheme
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class SettingsTest {
    @get:Rule val directory = TemporaryFolder()

    @Test fun preferencesPersistAfterStoreReopensAndResetRestoresDefaults() = runTest {
        val file = directory.root.resolve("appearance.preferences_pb")
        var scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        val expected = AppSettings(Appearance.Dark, AppPalette.Forest, false, AppFont.Serif, AppShape.Square)
        try {
            var repository = DataStoreSettingsRepository(PreferenceDataStoreFactory.create(scope = scope) { file })
            assertEquals(AppSettings(), repository.settings.first())
            repository.update { expected }
            assertEquals(expected, repository.settings.first())
            scope.coroutineContext[Job]!!.cancelAndJoin()
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            repository = DataStoreSettingsRepository(PreferenceDataStoreFactory.create(scope = scope) { file })
            assertEquals(expected, repository.settings.first())
            repository.reset()
            assertEquals(AppSettings(), repository.settings.first())
        } finally { scope.coroutineContext[Job]!!.cancelAndJoin() }
    }

    @Test fun unknownStoredOptionsFallBackAndConcurrentEditsPreserveOtherFields() = runTest {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            val store = PreferenceDataStoreFactory.create(scope = scope) { directory.root.resolve("fallback.preferences_pb") }
            store.edit { it[stringPreferencesKey("appearance")] = "RemovedOption" }
            val repository = DataStoreSettingsRepository(store)
            assertEquals(Appearance.System, repository.settings.first().appearance)
            coroutineScope {
                launch { repository.update { it.copy(font = AppFont.Serif) } }
                launch { repository.update { it.copy(palette = AppPalette.Ocean) } }
            }
            assertEquals(AppFont.Serif, repository.settings.first().font)
            assertEquals(AppPalette.Ocean, repository.settings.first().palette)
        } finally { scope.coroutineContext[Job]!!.cancelAndJoin() }
    }

    @Test fun explicitAppearanceOverridesSystemAndSystemModeFollowsIt() {
        assertTrue(AppSettings().useDarkTheme(true))
        assertFalse(AppSettings().useDarkTheme(false))
        assertTrue(AppSettings(appearance = Appearance.Dark).useDarkTheme(false))
        assertFalse(AppSettings(appearance = Appearance.Light).useDarkTheme(true))
    }
}
