package com.example.androidjetpackcomposeexamples

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.androidjetpackcomposeexamples.data.Photo
import com.example.androidjetpackcomposeexamples.data.PhotoRepository
import com.example.androidjetpackcomposeexamples.ui.theme.AndroidJetpackComposeExamplesTheme
import com.example.androidjetpackcomposeexamples.views.ExamplesApp
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ExamplesNavigationTest {
    @get:Rule val compose = createComposeRule()
    @Before fun showApp() {
        val fake = object : PhotoRepository {
            override suspend fun getPage(page: Int, refresh: Boolean) =
                listOf(Photo("0", "Test photographer"), Photo("1", "Second photographer"))
        }
        compose.setContent { AndroidJetpackComposeExamplesTheme { ExamplesApp(photoRepository = fake) } }
    }

    @Test fun advancedUiOpensLocationAndMapWithoutRequestingPermission() {
        compose.onNodeWithText("Advanced UI").performClick()
        compose.onNodeWithText("Location and Map").performClick()
        compose.onNodeWithText("Use my location").assertExists()
        compose.onNodeWithText("Location settings").assertExists()
        compose.onNodeWithText("App permissions").assertExists()
        if (!BuildConfig.MAPS_CONFIGURED) {
            compose.onNodeWithText("Google Maps setup required").assertExists()
        }
        compose.onNodeWithContentDescription("Navigate back").performClick()
        compose.onNodeWithText("Location and Map").assertExists()
    }

    @Test fun componentExamplesHandleButtonClicksAndTextInput() {
        compose.onNodeWithText("Components Playground").performClick()
        compose.onNodeWithText("Buttons").performClick()
        compose.onNodeWithText("Filled").performClick()
        compose.onNodeWithText("Clicks: 1 · Favorite: off").assertExists()
        compose.onNodeWithText("Simulate save").performScrollTo().assertExists()
        compose.onNodeWithContentDescription("Navigate back").performClick()
        compose.onNodeWithText("Text fields").performClick()
        compose.onNodeWithText("Name · filled").performTextInput("Compose")
        compose.onNodeWithText("Email · outlined").performTextInput("invalid")
        compose.onNodeWithText("Enter a valid email address").assertExists()
    }

    @Test fun interactiveListSupportsAddSelectDeleteAndBack() {
        compose.onNodeWithText("Lists and Grids").performClick()
        compose.onNodeWithText("Interactive list").performClick()
        compose.onNodeWithText("New item").performTextInput("Test item")
        compose.onNodeWithText("Add item").performClick()
        compose.onNodeWithText("6 items · 0 selected").assertExists()
        compose.onNodeWithText("Item 1").performClick()
        compose.onNodeWithText("6 items · 1 selected").assertExists()
        compose.onNodeWithText("Delete selected").performClick()
        compose.onNodeWithText("5 items · 0 selected").assertExists()
        compose.onNodeWithContentDescription("Navigate back").performClick()
        compose.onNodeWithText("Photo grid").assertExists()
    }

    @Test fun photoListAndGridDestinationsRenderRepositoryData() {
        compose.onNodeWithText("Lists and Grids").performClick()
        compose.onNodeWithText("Photo list").performClick()
        compose.onNodeWithText("Test photographer").assertExists()
        compose.onNodeWithText("2 photos").assertExists()
        compose.onNodeWithContentDescription("Navigate back").performClick()
        compose.onNodeWithText("Photo grid").performClick()
        compose.onNodeWithText("Test photographer").assertExists()
    }

    @Test fun carouselDestinationsRenderAndPagerSwipes() {
        compose.onNodeWithText("Carousels").performClick()
        compose.onNodeWithText("Horizontal pager").performClick()
        compose.onNodeWithText("Page 1 of 2").assertExists()
        compose.onNode(hasScrollToIndexAction()).performTouchInput { swipeLeft() }
        compose.onNodeWithText("Page 2 of 2").assertExists()
        compose.onNodeWithContentDescription("Navigate back").performClick()
        compose.onNodeWithText("LazyRow carousel").performClick()
        compose.onNodeWithText("Test photographer").assertExists()
    }
}
