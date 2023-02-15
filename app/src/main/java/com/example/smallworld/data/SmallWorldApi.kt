package com.example.smallworld.data

import com.example.smallworld.data.auth.models.*
import com.example.smallworld.data.search.models.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SmallWorldApi {
    @POST("auth/signup")
    suspend fun signUp(@Body user: SignUpDto): JwtDto

    @POST("auth/signin")
    suspend fun signIn(@Body signInDto: SignInDto): JwtDto

    @POST("auth/validate-signup")
    suspend fun validateEmailPassword(
        @Body validateEmailPasswordDto: ValidateEmailPasswordDto
    ): ValidateEmailPasswordResponseDto

    @POST("auth/validate-signup")
    suspend fun validateUsername(
        @Body validateUsernameDto: ValidateUsernameDto
    ): ValidateUsernameResponseDto

    @GET("/search/{query}")
    suspend fun search(@Path("query") query: String): List<UserDto>
}