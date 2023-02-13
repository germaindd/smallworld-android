package com.example.smallworld.ui.map

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val mapRoute = "map"

fun NavGraphBuilder.mapScreen() = composable(mapRoute) {
    MapScreen()
}