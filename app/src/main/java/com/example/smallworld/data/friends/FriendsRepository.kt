package com.example.smallworld.data.friends

import com.example.smallworld.data.SmallWorldApi
import com.example.smallworld.data.friends.dto.AcceptRequestDto
import com.example.smallworld.data.friends.dto.SendRequestDto
import com.example.smallworld.data.friends.dto.toFriendRequest
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

    suspend fun acceptRequest(userId: String) = withContext(dispatcher) {
        api.acceptRequest(AcceptRequestDto(userId))
    }

    suspend fun getRequests() = withContext(dispatcher) {
        api.getRequests().map { it.toFriendRequest() }
    }
}