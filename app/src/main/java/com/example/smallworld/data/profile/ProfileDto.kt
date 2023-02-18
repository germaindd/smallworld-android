package com.example.smallworld.data.profile

import com.example.smallworld.data.profile.models.FriendshipStatus

data class ProfileDto(
    val userId: String,
    val username: String,
    val friendshipStatus: FriendshipStatus
)
