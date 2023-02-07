package com.example.smallworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
fun SmallWorldApp(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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
}

