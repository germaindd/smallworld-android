package com.example.smallworld

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smallworld.ui.SignUpScreen
import com.example.smallworld.ui.SignUpViewModel
import com.example.smallworld.ui.theme.SmallWorldTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmallWorldTheme {
                // A surface container using the 'background' color from the theme
                SmallWorldApp(signUpViewModel)
            }
        }
    }

}

@Composable
fun SmallWorldApp(signUpViewModel: SignUpViewModel) {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        SmallWorldNavHost(navController, signUpViewModel)
    }
}

@Composable
fun SmallWorldNavHost(
    navHostController: NavHostController,
    signUpViewModel: SignUpViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navHostController, startDestination = "signup", modifier = modifier
    ) {
        composable("signup") {
            SignUpScreen(signUpViewModel)
        }
    }
}