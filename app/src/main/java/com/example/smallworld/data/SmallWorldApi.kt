package com.example.smallworld.data

import com.example.smallworld.data.auth.models.*
import com.example.smallworld.data.search.models.UserDto
import retrofit2.http.GET
import retrofit2.http.Path

interface SmallWorldApi {
    @GET("/search/{query}")
    suspend fun search(@Path("query") query: String): List<UserDto>
}