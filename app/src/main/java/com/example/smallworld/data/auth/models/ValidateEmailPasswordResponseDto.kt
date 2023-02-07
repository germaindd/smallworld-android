package com.example.smallworld.data.auth.models

data class ValidateEmailPasswordResponseDto(
    val email: SignUpValidationResult,
    val password: SignUpValidationResult
)