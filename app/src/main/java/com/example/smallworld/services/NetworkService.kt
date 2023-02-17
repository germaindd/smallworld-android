package com.example.smallworld.services

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.example.smallworld.di.ApplicationScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkService @Inject constructor(
    @ApplicationContext context: Context,
    @ApplicationScope private val coroutineScope: CoroutineScope
) {
    // callback below will get called immediately and update
    private val _isOnline = MutableStateFlow<Boolean>(false)
    val isOnlineStateFlow: StateFlow<Boolean> get() = _isOnline

    init {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                ) {
                    coroutineScope.launch { _isOnline.value = true }
                }
            }

            override fun onLost(network: Network) {
                coroutineScope.launch { _isOnline.value = false }
            }
        })
    }
}