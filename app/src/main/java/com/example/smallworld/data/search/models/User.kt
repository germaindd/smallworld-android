package com.example.smallworld.data.search.models

data class User(
    val id: String,
    val username: String
)

fun UserDto.toUser() = User(id, username)