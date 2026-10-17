package com.example.androidjetpackcomposeexamples

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.androidjetpackcomposeexamples.activity.MainActivity
import com.example.androidjetpackcomposeexamples.data.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Rule
import org.junit.Test

class SettingsInteractionTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val repository get() = (compose.activity.application as ExamplesApplication).settingsRepository
    @After fun restoreDefaults() { runBlocking { repository.reset() } }

    @Test fun settingsUpdateImmediatelySurviveRecreationAndReset() {
        runBlocking { repository.reset() }
        compose.onNodeWithText("Settings").performClick()
        compose.onNodeWithText("Dark").performClick()
        compose.onNodeWithText("Dark").assertIsSelected()
        compose.onNodeWithContentDescription("Dynamic colors").performClick()
        compose.onNodeWithText("Ocean").performClick()
        compose.onNodeWithText("Serif").performScrollTo().performClick()
        compose.onNodeWithText("Square").performScrollTo().performClick()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("Square").performScrollTo().assertIsSelected()
        compose.onNodeWithText("Serif").performScrollTo().assertIsSelected()
        compose.onNodeWithText("Ocean").performScrollTo().assertIsSelected()
        compose.onNodeWithText("Dark").performScrollTo().assertIsSelected()
        compose.onNodeWithText("Reset to defaults").performScrollTo().performClick()
        compose.onNodeWithText("System default").performScrollTo().assertIsSelected()
        compose.onNodeWithContentDescription("Dynamic colors").assertIsOn()
        compose.onNodeWithContentDescription("Navigate back").performClick()
        compose.onNodeWithText("Components Playground").assertExists()
    }
}
