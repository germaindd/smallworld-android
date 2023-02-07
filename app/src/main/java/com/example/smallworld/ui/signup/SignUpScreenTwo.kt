package com.example.smallworld.ui.signup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smallworld.R
import com.example.smallworld.data.auth.models.SignUpValidationResult
import com.example.smallworld.ui.theme.SmallWorldTheme

@Composable
fun SignUpScreenTwo(
    viewModel: SignUpViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SignUpScreenTwoContent(
        username = viewModel.username.collectAsStateWithLifecycle().value,
        onUsernameChange = viewModel::onUsernameChange,
        onSubmit = viewModel::onScreenTwoSubmit,
        onBackClick = onBackClick,
        usernameError = viewModel.usernameError.collectAsStateWithLifecycle().value,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignUpScreenTwoContent(
    username: String,
    onUsernameChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBackClick: () -> Unit,
    usernameError: SignUpValidationResult?,
    modifier: Modifier = Modifier,
) {
    val usernameFiledFocusRequester = remember { FocusRequester() }
    LaunchedEffect(usernameFiledFocusRequester) { usernameFiledFocusRequester.requestFocus() }
    Scaffold(
        topBar = { SignUpAppBar(onBackClick = onBackClick) },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            UsernameTextField(
                value = username,
                onEmailChange = onUsernameChange,
                isError = usernameError != null,
                onKeyboardDone = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(usernameFiledFocusRequester),
                supportingText = when (usernameError) {
                    SignUpValidationResult.INVALID_FORMAT -> {
                        { Text(text = stringResource(R.string.sign_up_invalid_username)) }
                    }
                    SignUpValidationResult.CONFLICT -> {
                        { Text(text = stringResource(R.string.sign_up_username_conflict)) }
                    }
                    SignUpValidationResult.SUCCESS,
                    null -> null
                },
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.sign_up_sign_up_button))
            }
        }
    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun UsernameTextField(
    value: String,
    onEmailChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean,
    onKeyboardDone: () -> Unit,
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
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onKeyboardDone() }),
        label = { Text(text = stringResource(R.string.sign_up_username_label)) }
    )
}


@Preview("Sign Up Screen", widthDp = 400, heightDp = 700, showBackground = true)
@Composable
private fun SignUpScreenPreview() {
    SmallWorldTheme {
        SignUpScreenTwoContent(
            username = "user",
            onUsernameChange = {},
            onSubmit = {},
            onBackClick = {},
            usernameError = SignUpValidationResult.CONFLICT
        )
    }
}