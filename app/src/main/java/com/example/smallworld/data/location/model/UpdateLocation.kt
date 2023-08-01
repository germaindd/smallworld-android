package com.example.smallworld.data.location.model

import com.example.smallworld.data.location.dto.UpdateLocationDto

data class UpdateLocation(
    val longitude: Double,
    val latitude: Double
)

fun UpdateLocation.toDto() = UpdateLocationDto(
    longitude = longitude,
    latitude = latitude
)