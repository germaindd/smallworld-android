package com.example.smallworld.data.friends

import com.example.smallworld.data.SmallWorldApi
import com.example.smallworld.data.friends.dto.SendRequestDto
import com.example.smallworld.di.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FriendsRepository @Inject constructor(
    private val api: SmallWorldApi,
    @DispatcherIO private val dispatcher: CoroutineDispatcher
) {
    suspend fun sendRequest(userId: String) = withContext(dispatcher) {
        api.sendRequest(SendRequestDto(userId))
    }
}