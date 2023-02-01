package com.example.smallworld

import com.example.smallworld.data.SmallWorldApi
import com.example.smallworld.data.models.SignUpDto
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: SmallWorldApi
) {
    suspend fun signUp(username: String, password: String) =
        api.signUp(SignUpDto(username, password))
}