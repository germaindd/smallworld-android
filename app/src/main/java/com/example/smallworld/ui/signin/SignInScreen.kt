package com.example.smallworld.ui.signin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smallworld.R
import com.example.smallworld.ui.sharedcomponents.PasswordTextField

@Composable
internal fun SignInScreen(
    viewModel: SignInViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    SignInScreenContent(
        onBackClick = onBackClick,
        usernameOrEmail = state.usernameOrEmail,
        onUsernameOrEmailChange = viewModel::onUsernameOrEmailChange,
        password = state.password,
        onPasswordChange = viewModel::onPasswordChange,
        onSubmit = viewModel::onSubmit,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignInScreenContent(
    onBackClick: () -> Unit,
    usernameOrEmail: String,
    modifier: Modifier = Modifier,
    onUsernameOrEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.sign_in_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.global_go_back)
                        )
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = usernameOrEmail,
                onValueChange = onUsernameOrEmailChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                label = { Text(text = stringResource(R.string.sign_in_username_email)) }
            )
            PasswordTextField(
                value = password,
                onPasswordChange = onPasswordChange,
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
                Text(text = stringResource(R.string.sign_sign_sign_in_button))
            }
        }
    }
}