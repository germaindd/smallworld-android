package com.example.smallworld.data.profile.model

import com.example.smallworld.data.profile.dto.ProfileDto
import com.example.smallworld.data.profile.enums.FriendshipStatus

data class Profile(
    val userId: String,
    val username: String,
    val friendshipStatus: FriendshipStatus
)

fun ProfileDto.toProfile(): Profile {
    return Profile(
        userId,
        username,
        friendshipStatus
    )
}