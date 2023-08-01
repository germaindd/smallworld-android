package com.example.smallworld.ui.flows.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable

const val homeScreenRoute = "home"

fun NavController.navigateToHome(builder: NavOptionsBuilder.() -> Unit) = navigate(homeScreenRoute, builder)

fun NavGraphBuilder.homeScreen() = composable(homeScreenRoute) {
    HomeScreen()
}