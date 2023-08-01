package com.example.smallworld.data.auth.dto

import com.example.smallworld.data.auth.enums.SignUpValidationResult

data class ValidateUsernameResponseDto(val username: SignUpValidationResult)