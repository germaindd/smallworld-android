package com.example.smallworld.ui.signup

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smallworld.R
import com.example.smallworld.data.auth.models.SignUpValidationResult
import com.example.smallworld.ui.theme.SmallWorldTheme

@Composable
internal fun SignUpScreenOne(
    viewModel: SignUpViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SignUpScreenOneContent(
        modifier = modifier,
        email = viewModel.email.collectAsState().value,
        onEmailChange = viewModel::onEmailChange,
        password = viewModel.password.collectAsState().value,
        onPasswordChange = viewModel::onPasswordChange,
        onNextClick = viewModel::onNextClick,
        onBackClick = onBackClick,
        emailError = viewModel.emailError.collectAsState().value,
        passwordError = viewModel.passwordError.collectAsState().value
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SignUpScreenOneContent(
    email: String,
    emailError: SignUpValidationResult?,
    onEmailChange: (String) -> Unit,
    password: String,
    passwordError: Boolean,
    onPasswordChange: (String) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Sign Up") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.global_go_back)
                            )
                        }
                    },
                )

            }
        ) { paddingValues ->
            Column(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                EmailTextField(
                    value = email,
                    onEmailChange = onEmailChange,
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailError != null,
                    supportingText = when (emailError) {
                        SignUpValidationResult.INVALID_FORMAT -> {
                            { Text(text = "Invalid Email") }
                        }
                        SignUpValidationResult.CONFLICT -> {
                            { Text(text = "User with that email already exists") }
                        }
                        SignUpValidationResult.SUCCESS,
                        null -> null
                    }
                )
                PasswordTextField(
                    value = password,
                    onPasswordChange = onPasswordChange,
                    isError = passwordError,
                    supportingText = if (passwordError) {
                        { Text(stringResource(R.string.sign_up_password_error_text)) }
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onNextClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.sign_up_next_button))
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EmailTextField(
    value: String,
    onEmailChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean,
    supportingText: @Composable (() -> Unit)?
) {
    OutlinedTextField(
        value = value,
        onValueChange = onEmailChange,
        isError = isError,
        supportingText = supportingText,
        modifier = modifier,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        label = { Text(text = stringResource(R.string.sign_up_email_label)) }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun PasswordTextField(
    value: String,
    onPasswordChange: (String) -> Unit,
    isError: Boolean,
    supportingText: @Composable (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        modifier = modifier,
        onValueChange = onPasswordChange,
        isError = isError,
        supportingText = supportingText,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
            keyboardType = KeyboardType.Password,
            // TODO get it so that done does the same thing as button
            imeAction = ImeAction.Done
        ),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        label = { Text(text = stringResource(R.string.sign_up_password_label)) },
        trailingIcon = {
            IconButton(
                onClick = { isPasswordVisible = !isPasswordVisible }
            ) {
                Icon(
                    imageVector = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                    contentDescription = stringResource(if (isPasswordVisible) R.string.sign_up_hide_password else R.string.sign_up_show_password)
                )
            }
        }
    )
}

@Preview
@Composable
fun EmailTextFieldPreview() {
    SmallWorldTheme {
        EmailTextField(value = "someemail@email.com", onEmailChange = {}, isError = false, supportingText = null)
    }
}

@Preview
@Composable
fun PasswordTextFieldPreview() {
    SmallWorldTheme {
        PasswordTextField(
            value = "somepassword",
            onPasswordChange = {},
            isError = false,
            supportingText = null
        )
    }
}

@Preview("Sign Up Screen", widthDp = 400, heightDp = 700, showBackground = true)
@Composable
fun SignUpScreenPreview() {
    SmallWorldTheme {
        SignUpScreenOneContent(
            email = "harry@gmail.com",
            onEmailChange = {},
            password = "password",
            onPasswordChange = {},
            onNextClick = {},
            onBackClick = {},
            emailError = SignUpValidationResult.CONFLICT,
            passwordError = true
        )
    }
}