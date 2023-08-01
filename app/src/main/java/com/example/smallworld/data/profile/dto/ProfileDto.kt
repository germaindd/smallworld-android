package com.example.smallworld.data.profile.dto

import com.example.smallworld.data.profile.enums.FriendshipStatus

data class ProfileDto(
    val userId: String,
    val username: String,
    val friendshipStatus: FriendshipStatus
)
