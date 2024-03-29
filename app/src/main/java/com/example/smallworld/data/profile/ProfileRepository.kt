package com.example.smallworld.data.profile

import com.example.smallworld.data.SmallWorldApi
import com.example.smallworld.data.profile.model.Profile
import com.example.smallworld.data.profile.model.toProfile
import com.example.smallworld.di.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val api: SmallWorldApi,
    @DispatcherIO private val dispatcher: CoroutineDispatcher
) {
    suspend fun getProfile(userId: String): Profile = withContext(dispatcher) {
        api.getProfile(userId).toProfile()
    }
}