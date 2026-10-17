package com.example.androidjetpackcomposeexamples.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.example.androidjetpackcomposeexamples.ExamplesApplication
import com.example.androidjetpackcomposeexamples.data.PhotoRepository
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.androidjetpackcomposeexamples.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamplesApp(
    modifier: Modifier = Modifier,
    photoRepository: PhotoRepository = (LocalContext.current.applicationContext as ExamplesApplication).photoRepository,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val titleRes = when {
        destination?.hasRoute<AppRoute.Home>() == true -> R.string.home_screen_title
        destination?.hasRoute<AppRoute.ComponentPlayground>() == true -> R.string.component_playground_screen_title
        destination?.hasRoute<AppRoute.Button>() == true -> R.string.button_screen_title
        destination?.hasRoute<AppRoute.TextFields>() == true -> R.string.text_fields_screen_title
        destination?.hasRoute<AppRoute.Settings>() == true -> R.string.settings_screen_title
        destination?.hasRoute<AppRoute.ListsAndGrids>() == true -> R.string.lists_grids_title
        destination?.hasRoute<AppRoute.EditableList>() == true -> R.string.editable_list_title
        destination?.hasRoute<AppRoute.PhotoList>() == true -> R.string.photo_list_title
        destination?.hasRoute<AppRoute.PhotoGrid>() == true -> R.string.photo_grid_title
        destination?.hasRoute<AppRoute.Carousels>() == true -> R.string.carousels_title
        destination?.hasRoute<AppRoute.PhotoPager>() == true -> R.string.photo_pager_title
        destination?.hasRoute<AppRoute.PhotoCarousel>() == true -> R.string.photo_carousel_title
        destination?.hasRoute<AppRoute.AdvancedUi>() == true -> R.string.advanced_ui_title
        destination?.hasRoute<AppRoute.LocationMap>() == true -> R.string.location_map_title
        else -> R.string.default_screen_title
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (navController.previousBackStackEntry != null) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Navigate back")
                        }
                    }
                },
                title = {
                    Text(
                        stringResource(titleRes),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            )
        },
    ) { innerPadding ->
        AppNavigator(
            navController = navController,
            photoRepository = photoRepository,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
