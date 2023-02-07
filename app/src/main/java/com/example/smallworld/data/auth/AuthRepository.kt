package com.example.smallworld.data.auth

import com.example.smallworld.data.SmallWorldApi
import com.example.smallworld.data.auth.models.*
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: SmallWorldApi
) {
    suspend fun signUp(username: String, password: String): JwtDto {
        return api.signUp(SignUpDto(username, password))
    }

    suspend fun validateEmailPassword(
        email: String,
        password: String
    ): ValidateEmailPasswordResponseDto {
        return api.validateEmailPassword(ValidateEmailPasswordDto(email, password))
    }

    suspend fun validateUsername(username: String): SignUpValidationResult {
        return api.validateUsername(ValidateUsernameDto(username)).username
    }
}