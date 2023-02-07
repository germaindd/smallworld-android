package com.example.smallworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.smallworld.ui.landing.landingScreenRoute
import com.example.smallworld.ui.landing.landingScreen
import com.example.smallworld.ui.signin.navigateToSignIn
import com.example.smallworld.ui.signin.signInScreen
import com.example.smallworld.ui.signup.navigateToSignUpGraph
import com.example.smallworld.ui.signup.signUpGraph
import com.example.smallworld.ui.theme.SmallWorldTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
fun SmallWorldApp() {
    val navController = rememberNavController()
    NavHost(
        navController = navController, startDestination = landingScreenRoute
    ) {
        landingScreen(
            onSignInButtonClick = navController::navigateToSignIn,
            onSignUpButtonClick = navController::navigateToSignUpGraph
        )
        signUpGraph(navController)
        signInScreen()
    }
}

