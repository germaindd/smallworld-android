package com.example.smallworld.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smallworld.ui.theme.SmallWorldTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                viewModel.username.collectAsState().value,
                onValueChange = viewModel::onUsernameChange,
                singleLine = true,
                placeholder = { Text(text = "Username") }
            )
            TextField(
                viewModel.password.collectAsState().value,
                onValueChange = viewModel::onPasswordChange,
                singleLine = true,
                placeholder = { Text(text = "Password") }
            )
            Button(onClick = viewModel::onSignUpClick) {
                Text(text = "Sign Up")
            }
        }
    }
}

@Preview("Sign Up Screen", widthDp = 320, heightDp = 700, showBackground = true)
@Composable
fun SignUpScreenPreview() {
    SmallWorldTheme {
//        SignUpScreen(viewModel = SignUpViewModel())
    }
}