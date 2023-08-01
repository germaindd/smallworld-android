package com.example.smallworld.ui.flows.signup

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.smallworld.ui.flows.home.navigateToHome
import com.example.smallworld.ui.flows.landing.landingScreenRoute
import com.example.smallworld.util.getViewModelScopedTo

private const val signUpGraphRoute = "sign_up_flow"
private const val screenOneRoute = "sign_up_screen_one"
private const val screenTwoRoute = "sign_up_screen_two"

fun NavController.navigateToSignUpGraph() = navigate(signUpGraphRoute)


@Composable
private fun NavBackStackEntry.signUpViewModel(
    navController: NavController
): SignUpViewModel = getViewModelScopedTo(
    scopeRoute = signUpGraphRoute,
    navController = navController
)

fun NavGraphBuilder.signUpGraph(navController: NavController) =
    navigation(startDestination = screenOneRoute, route = signUpGraphRoute) {
        composable(screenOneRoute) { backStackEntry ->
            val viewModel = backStackEntry.signUpViewModel(navController)
            LaunchedEffect(viewModel) {
                viewModel.onScreenOneSuccess.collect { navController.navigate(screenTwoRoute) }
            }
            SignUpScreenOne(viewModel, onBackClick = { navController.popBackStack() })
        }
        composable(screenTwoRoute) { backStackEntry ->
            val viewModel = backStackEntry.signUpViewModel(navController)
            LaunchedEffect(key1 = viewModel) {
                viewModel.onSignUpSuccess.collect {
                    navController.navigateToHome {
                        popUpTo(landingScreenRoute) { inclusive = true }
                    }
                }
            }
            SignUpScreenTwo(viewModel = viewModel, onBackClick = { navController.popBackStack() })
        }
    }
