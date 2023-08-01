package com.example.smallworld.ui.flows.signup

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.smallworld.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpAppBar(onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    TopAppBar(
        title = { Text(stringResource(R.string.sign_up_app_bar_title)) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.global_go_back)
                )
            }
        },
        modifier = modifier
    )
}