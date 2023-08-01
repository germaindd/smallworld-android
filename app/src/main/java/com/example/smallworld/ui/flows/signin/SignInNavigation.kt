package com.example.smallworld.ui.flows.signin

import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

private const val signInRoute = "sign_in"

fun NavController.navigateToSignIn() = navigate(signInRoute)

fun NavGraphBuilder.signInScreen(onBackClick: () -> Unit, onSignInSuccess: () -> Unit) =
    composable(signInRoute) {
        val viewModel: SignInViewModel = hiltViewModel()
        LaunchedEffect(viewModel) {
            viewModel.onSignInSuccess.collect { onSignInSuccess() }
        }
        SignInScreen(viewModel = viewModel, onBackClick = onBackClick)
    }
