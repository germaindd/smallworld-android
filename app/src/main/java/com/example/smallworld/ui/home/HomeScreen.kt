package com.example.smallworld.ui.home

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smallworld.ui.map.MapScreen

private enum class SelectedScreen(val icon: ImageVector, val contentDescription: String) {
    MAP(Icons.Filled.Public, "View Map"),
    NOTIFICATIONS(Icons.Filled.Notifications, "Notifications"),
    SETTINGS(Icons.Filled.Settings, "Settings")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val selected = remember {
        mutableStateOf(SelectedScreen.MAP)
    }
    Scaffold(
        bottomBar = {
            NavigationBar {
                SelectedScreen.values().forEach {
                    NavigationBarItem(
                        selected = selected.value == it,
                        onClick = { selected.value = it },
                        icon = { Icon(it.icon, contentDescription = it.contentDescription) }
                    )
                }
            }
        },
        modifier = modifier,
        contentWindowInsets = WindowInsets(0) // see SnackbarContainer
    ) { paddingValues ->
        when (selected.value) {
            SelectedScreen.MAP ->
                MapScreen(
                    hiltViewModel(),
                    Modifier
                        .padding(paddingValues)
                        .consumeWindowInsets(paddingValues)
                )
            SelectedScreen.NOTIFICATIONS -> Text("Yet to implement")
            SelectedScreen.SETTINGS -> Text("Yet to implement")
        }
    }
}