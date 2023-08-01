package com.example.smallworld.ui.flows.signup

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smallworld.R
import com.example.smallworld.data.auth.models.SignUpValidationResult
import com.example.smallworld.ui.components.PasswordTextField
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
        onSubmit = viewModel::onScreenOneSubmit,
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
    onSubmit: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = { SignUpAppBar(onBackClick = onBackClick) },
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0) // see SnackbarContainer
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .imePadding()
                .padding(16.dp)
                .fillMaxSize()
        ) {
            EmailTextField(
                value = email,
                onEmailChange = onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                isError = emailError != null,
                supportingText = when (emailError) {
                    SignUpValidationResult.INVALID_FORMAT -> {
                        { Text(text = stringResource(R.string.sign_up_invalid_email)) }
                    }
                    SignUpValidationResult.CONFLICT -> {
                        { Text(text = stringResource(R.string.sign_up_email_conflict)) }
                    }
                    else -> null
                }
            )
            PasswordTextField(
                value = password,
                onPasswordChange = onPasswordChange,
                isError = passwordError,
                supportingText = if (passwordError) {
                    { Text(stringResource(R.string.sign_up_password_error_text)) }
                } else null,
                onKeyboardDone = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.sign_up_next_button))
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

@Preview
@Composable
private fun EmailTextFieldPreview() {
    SmallWorldTheme {
        EmailTextField(
            value = "someemail@email.com",
            onEmailChange = {},
            isError = false,
            supportingText = null
        )
    }
}

@Preview("Sign Up Screen", widthDp = 400, heightDp = 700, showBackground = true)
@Composable
private fun SignUpScreenPreview() {
    SmallWorldTheme {
        SignUpScreenOneContent(
            email = "harry@gmail.com",
            onEmailChange = {},
            password = "password",
            onPasswordChange = {},
            onSubmit = {},
            onBackClick = {},
            emailError = SignUpValidationResult.CONFLICT,
            passwordError = true
        )
    }
}