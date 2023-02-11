package com.example.smallworld.ui.snackbar

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.smallworld.R

@StringRes
private fun getStringResource(snackBarMessage: SnackBarMessage) =
    when (snackBarMessage) {
        SnackBarMessage.SIGN_IN_ERROR_UNAUTHORIZED -> R.string.sign_in_invalid_sign_in
        SnackBarMessage.SIGN_IN_ERROR_UNKNOWN -> R.string.sign_in_something_went_wrong
        SnackBarMessage.SIGN_UP_ERROR_UNKNOWN -> R.string.sign_up_unknown_error
        SnackBarMessage.NO_NETWORK -> R.string.global_snackbar_check_network
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnackBarContainer(
    viewModel: SnackBarViewModel,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel) {
        viewModel.messagesFlow.collect { snackBarMessage ->
            snackBarHostState.showSnackbar(context.getString(getStringResource(snackBarMessage)))
        }
    }
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
    ) { paddingValues ->
        Box(modifier = modifier.padding(paddingValues), content = content)
    }
}