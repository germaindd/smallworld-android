package com.example.smallworld.ui.landing

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable

const val landingScreenRoute = "landing"

fun NavController.navigateToLanding(builder: NavOptionsBuilder.() -> Unit) =
    navigate(landingScreenRoute, builder)

fun NavGraphBuilder.landingScreen(
    onSignInButtonClick: () -> Unit,
    onSignUpButtonClick: () -> Unit
) =
    composable(landingScreenRoute) {
        LandingScreen(
            onSignInButtonClick = onSignInButtonClick,
            onSignUpButtonClick = onSignUpButtonClick
        )
    }
