package com.example.smallworld.data

import com.example.smallworld.data.models.JwtDto
import com.example.smallworld.data.models.SignUpDto
import retrofit2.http.Body
import retrofit2.http.POST

interface SmallWorldApi {
    @POST("auth/signup")
    suspend fun signUp(@Body user: SignUpDto): JwtDto
}