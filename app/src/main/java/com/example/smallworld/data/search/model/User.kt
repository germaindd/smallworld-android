package com.example.smallworld.data.search.model

import com.example.smallworld.data.search.dto.UserDto

data class User(
    val id: String,
    val username: String
)

fun UserDto.toUser() = User(id, username)