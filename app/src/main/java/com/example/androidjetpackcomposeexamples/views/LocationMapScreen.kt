package com.example.androidjetpackcomposeexamples.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.androidjetpackcomposeexamples.BuildConfig
import com.example.androidjetpackcomposeexamples.data.DeviceLocationProvider
import com.example.androidjetpackcomposeexamples.viewmodel.LocationMapViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.util.Locale

@Composable
fun LocationMapRoute() {
    val context = LocalContext.current.applicationContext
    val model: LocationMapViewModel = viewModel(factory = viewModelFactory {
        initializer { LocationMapViewModel(DeviceLocationProvider(context)) }
    })
    LocationMapScreen(model)
}

@Composable
fun LocationMapScreen(
    viewModel: LocationMapViewModel,
    mapsConfigured: Boolean = BuildConfig.MAPS_CONFIGURED,
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val state by viewModel.state.collectAsStateWithLifecycle()
    fun granted(permission: String) = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    var fine by remember { mutableStateOf(granted(Manifest.permission.ACCESS_FINE_LOCATION)) }
    var coarse by remember { mutableStateOf(granted(Manifest.permission.ACCESS_COARSE_LOCATION)) }
    var denied by rememberSaveable { mutableStateOf(false) }
    var servicesAvailable by remember { mutableStateOf(false) }
    val permissions = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { _ ->
        fine = granted(Manifest.permission.ACCESS_FINE_LOCATION)
        coarse = granted(Manifest.permission.ACCESS_COARSE_LOCATION)
        denied = !fine && !coarse
        if (fine || coarse) viewModel.locate()
    }
    DisposableEffect(lifecycle, viewModel) {
        fun synchronize() {
            fine = granted(Manifest.permission.ACCESS_FINE_LOCATION)
            coarse = granted(Manifest.permission.ACCESS_COARSE_LOCATION)
            servicesAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
            if (!fine && !coarse) viewModel.permissionRevoked()
        }
        synchronize()
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) synchronize()
            if (event == Lifecycle.Event.ON_STOP) viewModel.stop()
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer); viewModel.stop() }
    }
    var satellite by rememberSaveable { mutableStateOf(false) }
    var pinLatitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var pinLongitude by rememberSaveable { mutableStateOf<Double?>(null) }
    var mapLoaded by remember { mutableStateOf(false) }
    val camera = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.7749, -122.4194), 11f)
    }
    LaunchedEffect(state.location, mapLoaded) {
        val fix = state.location
        if (fix != null && mapLoaded) camera.animate(CameraUpdateFactory.newLatLngZoom(LatLng(fix.latitude, fix.longitude), if (fine) 15f else 12f))
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Location and Map", style = MaterialTheme.typography.headlineSmall)
        Text("Fetch a single foreground location, center the map, and long-press to place a pin.")
        Text(when { fine -> "Permission: precise"; coarse -> "Permission: approximate"; else -> "Permission: not granted" })
        Button(
            enabled = !state.loading && servicesAvailable,
            onClick = {
                if (fine || coarse) viewModel.locate()
                else permissions.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            },
        ) { Text(if (state.loading) "Finding location…" else "Use my location") }
        if (state.loading) LinearProgressIndicator(Modifier.fillMaxWidth())
        if (denied) Text("Location access was denied. You can still browse the map. To allow access, retry or open App permissions.")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = { context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }) { Text("Location settings") }
            TextButton(onClick = { context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"))) }) { Text("App permissions") }
        }
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        state.location?.let {
            Text(String.format(Locale.getDefault(), "Latitude: %.5f\nLongitude: %.5f\nAccuracy: ±%.0f m", it.latitude, it.longitude, it.accuracyMeters))
        }
        when {
            !mapsConfigured -> Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Google Maps setup required", style = MaterialTheme.typography.titleMedium)
                    Text("Add MAPS_API_KEY to local.properties and rebuild. Enable Maps SDK for Android in your Google Cloud project. Location requests work independently of the map key.")
                }
            }
            !servicesAvailable -> Text("Google Play services is unavailable. Use a supported device or Google APIs emulator.")
            else -> {
                Row {
                    Text("Satellite map", Modifier.weight(1f))
                    Switch(satellite, { satellite = it })
                }
                GoogleMap(
                    modifier = Modifier.fillMaxWidth().height(360.dp),
                    cameraPositionState = camera,
                    properties = MapProperties(mapType = if (satellite) MapType.SATELLITE else MapType.NORMAL),
                    uiSettings = MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = false),
                    onMapLoaded = { mapLoaded = true },
                    onMapLongClick = { pinLatitude = it.latitude; pinLongitude = it.longitude },
                ) {
                    state.location?.let { fix ->
                        Marker(
                            state = rememberUpdatedMarkerState(position = LatLng(fix.latitude, fix.longitude)),
                            title = "Your location", snippet = "Accuracy: ±${fix.accuracyMeters.toInt()} m",
                        )
                    }
                    if (pinLatitude != null && pinLongitude != null) {
                        Marker(state = rememberUpdatedMarkerState(position = LatLng(pinLatitude!!, pinLongitude!!)), title = "Selected point")
                    }
                }
                if (pinLatitude != null) {
                    Text(String.format(Locale.getDefault(), "Selected pin: %.5f, %.5f", pinLatitude, pinLongitude))
                    TextButton(onClick = { pinLatitude = null; pinLongitude = null }) { Text("Clear pin") }
                }
            }
        }
        if (!servicesAvailable) Text("Location requests require Google Play services.")
    }
}
