package com.example.smallworld

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.smallworld.ui.home.homeScreen
import com.example.smallworld.ui.home.navigateToHome
import com.example.smallworld.ui.landing.landingScreen
import com.example.smallworld.ui.landing.landingScreenRoute
import com.example.smallworld.ui.landing.navigateToLanding
import com.example.smallworld.ui.signin.navigateToSignIn
import com.example.smallworld.ui.signin.signInScreen
import com.example.smallworld.ui.signup.navigateToSignUpGraph
import com.example.smallworld.ui.signup.signUpGraph
import com.example.smallworld.ui.snackbar.SnackBarContainer
import com.example.smallworld.ui.splash.splashScreen
import com.example.smallworld.ui.splash.splashScreenRoute
import com.example.smallworld.ui.theme.SmallWorldTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmallWorldTheme {
                SmallWorldApp()
            }
        }
    }

}

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
                        navController.navigateToHome {
                            popUpTo(splashScreenRoute) { inclusive = true }
                        }
                    },
                    onUserIsSignedOut = {
                        navController.navigateToLanding {
                            popUpTo(splashScreenRoute) { inclusive = true }
                        }
                    }
                )
                landingScreen(
                    onSignInButtonClick = navController::navigateToSignIn,
                    onSignUpButtonClick = navController::navigateToSignUpGraph
                )
                signUpGraph(navController)
                signInScreen(
                    onSignInSuccess = {
                        navController.navigateToHome {
                            popUpTo(landingScreenRoute) { inclusive = true }
                        }
                    }, onBackClick = { navController.popBackStack() }
                )
                homeScreen()
            }
        }
    }
}

