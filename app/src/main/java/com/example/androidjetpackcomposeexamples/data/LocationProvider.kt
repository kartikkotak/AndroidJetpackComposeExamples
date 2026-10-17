package com.example.androidjetpackcomposeexamples.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class LocationFix(val latitude: Double, val longitude: Double, val accuracyMeters: Float)

fun interface LocationProvider {
    suspend fun currentLocation(): LocationFix?
}

class DeviceLocationProvider(context: Context) : LocationProvider {
    private val context = context.applicationContext

    override suspend fun currentLocation(): LocationFix? {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!fine && !coarse) throw SecurityException("Location permission is required")
        check(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
            "Google Play services is unavailable. Use a supported device or Google APIs emulator."
        }
        check(context.getSystemService(LocationManager::class.java).isLocationEnabled) {
            "Location is turned off. Enable it in Location settings and try again."
        }
        val request = CurrentLocationRequest.Builder()
            .setPriority(if (fine) Priority.PRIORITY_HIGH_ACCURACY else Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            .setMaxUpdateAgeMillis(5_000)
            .setDurationMillis(10_000)
            .build()
        return withTimeoutOrNull(15_000) {
            suspendCancellableCoroutine { continuation ->
                val cancellation = CancellationTokenSource()
                continuation.invokeOnCancellation { cancellation.cancel() }
                try {
                    // Permission can be revoked while this request is starting; surface that to the UI.
                    LocationServices.getFusedLocationProviderClient(context)
                        .getCurrentLocation(request, cancellation.token)
                        .addOnSuccessListener { location ->
                            if (continuation.isActive) continuation.resume(location?.let {
                                LocationFix(it.latitude, it.longitude, it.accuracy)
                            })
                        }
                        .addOnFailureListener { error ->
                            if (continuation.isActive) continuation.resumeWithException(error)
                        }
                        .addOnCanceledListener { continuation.cancel() }
                } catch (error: SecurityException) {
                    if (continuation.isActive) continuation.resumeWithException(error)
                }
            }
        }
    }
}
