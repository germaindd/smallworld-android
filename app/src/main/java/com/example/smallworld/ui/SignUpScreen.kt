package com.example.smallworld.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smallworld.ui.theme.SmallWorldTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(modifier: Modifier = Modifier) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Box(contentAlignment = Alignment.Center, modifier = modifier.fillMaxSize()) {
        Column {
            TextField(
                username,
                onValueChange = { value: String -> username = value },
                singleLine = true,
                placeholder = { Text(text = "Username") }
            )
            Spacer(modifier = Modifier.size(8.dp))
            TextField(
                password,
                onValueChange = { password = it },
                singleLine = true,
                placeholder = { Text(text = "Password") }
            )
        }
    }
}

@Preview("Sign Up Screen", widthDp = 320, heightDp = 700, showBackground = true)
@Composable
fun SignUpScreenPreview() {
    SmallWorldTheme {
        SignUpScreen()
    }
}