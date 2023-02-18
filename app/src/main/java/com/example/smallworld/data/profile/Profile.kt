package com.example.smallworld.data.profile

import com.example.smallworld.data.profile.models.FriendshipStatus

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