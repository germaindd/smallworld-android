package com.example.smallworld.data

import com.example.smallworld.data.auth.models.*
import com.example.smallworld.data.friends.SendRequestDto
import com.example.smallworld.data.profile.ProfileDto
import com.example.smallworld.data.search.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SmallWorldApi {
    @GET("/search/{query}")
    suspend fun search(@Path("query") query: String): List<UserDto>

    @GET("/profile/{userId}")
    suspend fun getProfile(@Path("userId") userId: String): ProfileDto

    @POST("friends/send-request")
    suspend fun sendRequest(@Body sendRequestDto: SendRequestDto)
}