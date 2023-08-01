package com.example.smallworld.data

import com.example.smallworld.data.auth.dto.*
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SmallWorldAuthApi {
    @POST("signup")
    suspend fun signUp(@Body user: SignUpDto): JwtDto

    @POST("signin")
    suspend fun signIn(@Body signInDto: SignInDto): JwtDto

    @POST("validate-signup")
    suspend fun validateEmailPassword(
        @Body validateEmailPasswordDto: ValidateEmailPasswordDto
    ): ValidateEmailPasswordResponseDto

    @POST("validate-signup")
    suspend fun validateUsername(
        @Body validateUsernameDto: ValidateUsernameDto
    ): ValidateUsernameResponseDto

    @POST("refresh-tokens")
    suspend fun refreshTokens(@Header("authorization") bearer: String): JwtDto
}