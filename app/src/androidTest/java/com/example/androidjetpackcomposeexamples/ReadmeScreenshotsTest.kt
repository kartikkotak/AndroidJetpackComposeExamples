package com.example.androidjetpackcomposeexamples

import android.content.ContentValues
import android.provider.MediaStore
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.androidjetpackcomposeexamples.activity.MainActivity
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test

/** Opt in with -Pandroid.testInstrumentationRunnerArguments.captureReadme=true. */
class ReadmeScreenshotsTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val exportPath = "Pictures/ComposeReadme/${System.currentTimeMillis()}/"

    @Test fun captureExampleScreens() {
        assumeTrue(InstrumentationRegistry.getArguments().getString("captureReadme") == "true")
        compose.waitUntil(15_000) { compose.onAllNodesWithText("Components Playground").fetchSemanticsNodes().isNotEmpty() }
        capture("home")
        open("Components Playground")
        open("Buttons")
        capture("buttons")
        back()
        open("Text fields")
        capture("text-fields")
        back(); back()
        open("Lists and Grids")
        open("Interactive list")
        compose.onNodeWithText("Item 1").performClick()
        capture("interactive-list")
        back()
        open("Photo list")
        waitForPhotos()
        capture("photo-list")
        back()
        open("Photo grid")
        waitForPhotos()
        capture("photo-grid")
        back(); back()
        open("Carousels")
        open("Horizontal pager")
        waitForPhotos()
        capture("horizontal-pager")
        back()
        open("LazyRow carousel")
        waitForPhotos()
        capture("lazy-row-carousel")
        back(); back()
        open("Advanced UI")
        open("Location and Map")
        capture("location-map")
        back(); back()
        open("Settings")
        compose.waitUntil(10_000) { compose.onAllNodesWithText("Appearance").fetchSemanticsNodes().isNotEmpty() }
        capture("settings")
        compose.onNodeWithText("Reset to defaults").performScrollTo()
        capture("settings-preview")
    }

    private fun open(label: String) { compose.onNodeWithText(label).performClick(); compose.waitForIdle() }
    private fun back() { compose.onNodeWithContentDescription("Navigate back").performClick(); compose.waitForIdle() }
    private fun waitForPhotos() {
        compose.waitUntil(45_000) { compose.onAllNodesWithText("Photo #0").fetchSemanticsNodes().isNotEmpty() }
        compose.waitUntil(45_000) { compose.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo)).fetchSemanticsNodes().isEmpty() }
        compose.onAllNodesWithText("Image unavailable").assertCountEquals(0)
    }
    private fun capture(name: String) {
        compose.mainClock.advanceTimeBy(1_000)
        compose.waitForIdle()
        // Native ripple rendering uses wall-clock time rather than the Compose test clock.
        android.os.SystemClock.sleep(300)
        val resolver = compose.activity.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$name.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, exportPath)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val uri = checkNotNull(resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))
        checkNotNull(resolver.openOutputStream(uri)).use {
            check(compose.onRoot().captureToImage().asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, it))
        }
        resolver.update(uri, ContentValues().apply { put(MediaStore.Images.Media.IS_PENDING, 0) }, null, null)
        android.util.Log.i("ReadmeScreenshots", "Saved $exportPath$name.png")
    }
}
