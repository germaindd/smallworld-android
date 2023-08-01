package com.example.smallworld.data.location

import com.example.smallworld.data.SmallWorldApi
import com.example.smallworld.data.location.model.Location
import com.example.smallworld.data.location.model.UpdateLocation
import com.example.smallworld.data.location.model.toDto
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

    suspend fun getFriendsLocations() = withContext(dispatcher) {
        api.getFriendsLocations().mapNotNull {
            if (it.latitude == null || it.longitude == null) null
            else Location(
                it.userId,
                it.longitude,
                it.latitude
            )
        }
    }
}