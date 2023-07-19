package com.example.smallworld.data.location

import com.example.smallworld.data.SmallWorldApi
import com.example.smallworld.di.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val api: SmallWorldApi,
    @DispatcherIO private val dispatcher: CoroutineDispatcher
) {
    suspend fun updateLocation(location: UpdateLocation) = withContext(dispatcher) {
        api.updateLocation(location.toDto())
    }
}