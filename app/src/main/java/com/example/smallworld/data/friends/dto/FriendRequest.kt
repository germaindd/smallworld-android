package com.example.smallworld.data.friends.dto

data class FriendRequest(
    val userId: String,
    val username: String
)

fun FriendRequestDto.toFriendRequest() = FriendRequest(
    userId,
    username
)