package com.example.smallworld.data.auth.dto

import com.example.smallworld.data.auth.enums.SignUpValidationResult

data class ValidateEmailPasswordResponseDto(
    val email: SignUpValidationResult,
    val password: SignUpValidationResult
)