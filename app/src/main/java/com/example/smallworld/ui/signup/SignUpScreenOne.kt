package com.example.smallworld.ui.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smallworld.R
import com.example.smallworld.ui.theme.SmallWorldTheme

@Composable
internal fun SignUpScreenOne(
    viewModel: SignUpViewModel,
    modifier: Modifier = Modifier,
) {
    SignUpScreenOneContent(
        modifier,
        viewModel.username.collectAsState().value,
        viewModel::onUsernameChange,
        viewModel.password.collectAsState().value,
        viewModel::onPasswordChange,
        viewModel::onSignUpClick,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SignUpScreenOneContent(
    modifier: Modifier = Modifier,
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onSignUpClick: () -> Unit
) {
    val isPasswordVisible = remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .paint(
                painterResource(id = R.drawable.sign_up_background),
                true,
                contentScale = ContentScale.Crop
            )
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .width(IntrinsicSize.Min)
        ) {
            Image(
                painter = painterResource(R.drawable.earth),
                contentDescription = null,
                modifier = Modifier
                    .size(150.dp)
                    .shadow(20.dp, RoundedCornerShape(75.dp), true)
            )
            OutlinedTextField(
                username,
                onValueChange = onUsernameChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                placeholder = { Text(text = "Email") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary.copy(0.7f),
                    placeholderColor = MaterialTheme.colorScheme.onPrimary.copy(0.7f),
                    focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    textColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onPrimary
                )
            )
            OutlinedTextField(
                password,
                onValueChange = onPasswordChange,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = if (isPasswordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                placeholder = { Text(text = "Password") },
                trailingIcon = {
                    Image(
                        painterResource(id = R.drawable.sign_up_show_password),
                        alpha = if (isPasswordVisible.value) 1.0f else 0.5f,
                        contentDescription = "Show Password",
                        modifier = Modifier
                            .clickable { isPasswordVisible.value = !isPasswordVisible.value }
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.onPrimary.copy(0.7f),
                    placeholderColor = MaterialTheme.colorScheme.onPrimary.copy(0.7f),
                    focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
                    textColor = MaterialTheme.colorScheme.onPrimary,
                    cursorColor = MaterialTheme.colorScheme.onPrimary
                )
            )
            Button(
                onClick = onSignUpClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Sign Up")
            }
        }
    }
}

@Preview("Sign Up Screen", widthDp = 400, heightDp = 700, showBackground = true)
@Composable
fun SignUpScreenPreview() {
    SmallWorldTheme {
        SignUpScreenOneContent(
            username = "harry@gmail.com",
            onUsernameChange = {},
            password = "password",
            onPasswordChange = {},
            onSignUpClick = {}
        )
    }
}