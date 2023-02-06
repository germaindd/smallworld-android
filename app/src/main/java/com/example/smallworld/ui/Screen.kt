package com.example.smallworld.ui

import androidx.navigation.NavController

enum class Screen(val route: String) {
    LANDING("landing"),
    SIGN_UP("sign_up"),
    SIGN_IN("log_in")
}

fun NavController.navigateToScreen(screen: Screen) = navigate(screen.route)