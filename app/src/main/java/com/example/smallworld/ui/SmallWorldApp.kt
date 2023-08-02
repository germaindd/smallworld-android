package com.example.smallworld.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.smallworld.ui.components.snackbar.SnackBarContainer
import com.example.smallworld.ui.flows.home.homeScreen
import com.example.smallworld.ui.flows.home.navigateToHome
import com.example.smallworld.ui.flows.landing.landingScreen
import com.example.smallworld.ui.flows.landing.navigateToLanding
import com.example.smallworld.ui.flows.landing.popUpToLandingScreenInclusive
import com.example.smallworld.ui.flows.signin.navigateToSignIn
import com.example.smallworld.ui.flows.signin.signInScreen
import com.example.smallworld.ui.flows.signup.navigateToSignUpGraph
import com.example.smallworld.ui.flows.signup.signUpGraph
import com.example.smallworld.ui.flows.splash.popUpToSplashScreenInclusive
import com.example.smallworld.ui.flows.splash.splashScreen
import com.example.smallworld.ui.flows.splash.splashScreenRoute

@Composable
fun SmallWorldApp(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SnackBarContainer(viewModel = hiltViewModel()) {
            val navController = rememberNavController()
            NavHost(
                navController = navController, startDestination = splashScreenRoute
            ) {
                splashScreen(
                    onUserIsSignedIn = {
                        navController.navigateToHome { popUpToSplashScreenInclusive() }
                    },
                    onUserIsSignedOut = {
                        navController.navigateToLanding { popUpToSplashScreenInclusive() }
                    }
                )
                landingScreen(
                    onSignInButtonClick = navController::navigateToSignIn,
                    onSignUpButtonClick = navController::navigateToSignUpGraph
                )
                signInScreen(
                    onSignInSuccess = { navController.navigateToHome { popUpToLandingScreenInclusive() } },
                    onBackClick = { navController.popBackStack() }
                )
                signUpGraph(navController)
                homeScreen()
            }
        }
    }
}