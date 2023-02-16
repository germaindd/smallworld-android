package com.example.smallworld.data.search

import com.example.smallworld.data.SmallWorldApi
import com.example.smallworld.data.search.models.User
import com.example.smallworld.data.search.models.toUser
import com.example.smallworld.di.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val api: SmallWorldApi,
    @DispatcherIO private val dispatcher: CoroutineDispatcher
) {
    suspend fun search(query: String): List<User> = withContext(dispatcher) {
        api.search(query).map { it.toUser() }
    }
}