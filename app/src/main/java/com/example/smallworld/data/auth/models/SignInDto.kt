package com.example.smallworld.data.auth.models

data class SignInDto(
    val usernameOrEmail: String,
    val password: String
)