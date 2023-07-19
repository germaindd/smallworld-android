package com.example.smallworld.ui.map

import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Class whose main function is to decouple the code from the origin of location data and adapt
 * Google Play Services' FusedLocationProvider's Task-based asynchronous model to kotlin coroutines
 * and keeping our core classes more concise.
 */
class LocationProvider @Inject constructor(@ApplicationContext context: Context) {
    private val fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    suspend fun getCurrentLocation(): Location {
        return suspendCoroutine {
            fusedLocationProviderClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                null
            ).addOnSuccessListener { location: Location -> it.resume(location) }
        }
    }
}