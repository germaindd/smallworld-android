package com.example.smallworld.data.auth.dto

data class SignInDto(
    val usernameOrEmail: String,
    val password: String
)