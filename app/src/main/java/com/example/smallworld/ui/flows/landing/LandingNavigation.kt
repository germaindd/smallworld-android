package com.example.smallworld.ui.flows.landing

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch

const val landingScreenRoute = "landing"

fun NavController.navigateToLanding(builder: NavOptionsBuilder.() -> Unit) =
    navigate(landingScreenRoute, builder)

fun NavGraphBuilder.landingScreen(
    onSignInButtonClick: () -> Unit,
    onSignUpButtonClick: () -> Unit
) =
    composable(landingScreenRoute) {
        val viewModel: LandingViewModel = hiltViewModel()
        LaunchedEffect(viewModel) {
            launch { viewModel.onNavigateToSignIn.collect { onSignInButtonClick() } }
            launch { viewModel.onNavigateToSignUp.collect { onSignUpButtonClick() } }
        }
        LandingScreen(viewModel = viewModel)
    }
