package com.example.smallworld.data.profile

data class ProfileDto(
    val userId: String,
    val username: String,
    val friendshipStatus: FriendshipStatus
)
