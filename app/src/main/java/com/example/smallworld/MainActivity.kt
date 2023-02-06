package com.example.smallworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smallworld.ui.Screen
import com.example.smallworld.ui.landing.LandingScreen
import com.example.smallworld.ui.navigateToScreen
import com.example.smallworld.ui.signin.SignInScreen
import com.example.smallworld.ui.signup.SignUpScreen
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
        navController = navController, startDestination = Screen.LANDING.route
    ) {
        composable(Screen.LANDING.route) {
            LandingScreen(
                onSignInButtonClick = { navController.navigateToScreen(Screen.SIGN_IN) },
                onSignUpButtonClick = { navController.navigateToScreen(Screen.SIGN_UP) })
        }
        composable(Screen.SIGN_UP.route) {
            SignUpScreen()
        }
        composable(Screen.SIGN_IN.route) {
            SignInScreen()
        }
    }
}

