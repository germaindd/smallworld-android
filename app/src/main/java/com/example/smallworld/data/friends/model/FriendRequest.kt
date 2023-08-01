package com.example.smallworld.data.friends.model

import com.example.smallworld.data.friends.dto.FriendRequestDto

data class FriendRequest(
    val userId: String,
    val username: String
)

fun FriendRequestDto.toFriendRequest() = FriendRequest(
    userId,
    username
)