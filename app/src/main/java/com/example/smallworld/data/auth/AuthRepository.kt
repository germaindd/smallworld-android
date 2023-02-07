package com.example.smallworld.data.auth

import com.example.smallworld.data.SmallWorldApi
import com.example.smallworld.data.auth.models.*
import com.example.smallworld.di.DispatcherIO
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: SmallWorldApi,
    @DispatcherIO private val dispatcher: CoroutineDispatcher
) {
    suspend fun signUp(username: String, password: String, email: String): JwtDto =
        withContext(dispatcher) {
            api.signUp(SignUpDto(username, password, email))
        }

    suspend fun validateEmailPassword(
        email: String,
        password: String
    ): ValidateEmailPasswordResponseDto =
        withContext(dispatcher) {
            api.validateEmailPassword(ValidateEmailPasswordDto(email, password))
        }

    suspend fun validateUsername(username: String): SignUpValidationResult =
        withContext(dispatcher) {
            api.validateUsername(ValidateUsernameDto(username)).username
        }
}