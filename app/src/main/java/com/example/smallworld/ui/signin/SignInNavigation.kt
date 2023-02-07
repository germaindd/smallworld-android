package com.example.smallworld.ui.signin

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

private const val signInRoute = "sign_in"

fun NavController.navigateToSignIn() = navigate(signInRoute)

fun NavGraphBuilder.signInScreen() = composable(signInRoute) {
    SignInScreen()
}
