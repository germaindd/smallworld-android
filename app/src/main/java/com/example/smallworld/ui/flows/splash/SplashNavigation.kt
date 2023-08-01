package com.example.smallworld.ui.flows.splash

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val splashScreenRoute = "splash"

fun NavGraphBuilder.splashScreen(onUserIsSignedIn: () -> Unit, onUserIsSignedOut: () -> Unit) =
    composable(splashScreenRoute) {
        val viewModel: SplashViewModel = hiltViewModel()
        LaunchedEffect(viewModel) {
            viewModel.onIsUserSignedIn.collect { isSignedIn ->
                if (isSignedIn) onUserIsSignedIn() else onUserIsSignedOut()
            }
        }
        SplashScreen()
    }