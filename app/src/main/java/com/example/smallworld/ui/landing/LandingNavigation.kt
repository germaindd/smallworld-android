package com.example.smallworld.ui.landing

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val landingScreenRoute = "landing"

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
