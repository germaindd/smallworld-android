package com.example.smallworld.ui.signup

import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation

private const val signUpGraphRoute = "sign_up_flow"
private const val screenOneRoute = "sign_up_screen_one"
private const val screenTwoRoute = "sign_up_screen_two"

fun NavController.navigateToSignUpGraph() = navigate(signUpGraphRoute)

fun NavGraphBuilder.signUpGraph(navController: NavController) =
    navigation(startDestination = screenOneRoute, route = signUpGraphRoute) {
        composable(screenOneRoute) { backStackEntry ->
            val navGraphEntry = remember(backStackEntry) {
                navController.getBackStackEntry(signUpGraphRoute)
            }
            val viewModel: SignUpViewModel = hiltViewModel(navGraphEntry)
            SignUpScreenOne(viewModel)
        }
        composable(screenTwoRoute) {
            Text("TODO")
        }
    }
