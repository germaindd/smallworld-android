package com.example.smallworld.ui.flows.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Utility class to request permissions using a coroutine-based API. */
class PermissionsManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private var launcher: ActivityResultLauncher<Array<String>>? = null
    private val resultChannel = Channel<Boolean>()

    fun init(fragment: Fragment) {
        launcher = fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionResults ->
            fragment.lifecycleScope.launch {
                resultChannel.send(locationPermissions.any { permissionResults[it] == true })
            }
        }
    }

    /** Returns true if the app has either ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION
     * permission or both */
    val hasLocationPermissions
        get() = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    suspend fun requestLocationPermissions(): Boolean = coroutineScope {
        launcher?.launch(locationPermissions) ?: error("init() has not been called")
        resultChannel.receive()
    }
}