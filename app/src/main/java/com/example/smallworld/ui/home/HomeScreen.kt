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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smallworld.R
import com.example.smallworld.ui.map.MapScreen
import com.example.smallworld.ui.map.MapViewModel
import com.example.smallworld.ui.notifications.NotificationsScreen
import com.example.smallworld.ui.notifications.NotificationsViewModel

private enum class SelectedScreen {
    MAP,
    NOTIFICATIONS,
    SETTINGS
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val selectedScreen = rememberSaveable {
        mutableStateOf(SelectedScreen.MAP)
    }

    val mapViewModel = hiltViewModel<MapViewModel>()
    val notificationsViewModel = hiltViewModel<NotificationsViewModel>()

    val notificationsState = notificationsViewModel.state.collectAsState()

    Scaffold(
        bottomBar = {
            HomeScreenNavigationBar(
                selectedScreen = selectedScreen,
                numberOfNotifications = notificationsState.value.numberOfNotifications
            )
        },
        modifier = modifier,
        contentWindowInsets = WindowInsets(0) // see SnackbarContainer
    ) { paddingValues ->
        when (selectedScreen.value) {
            SelectedScreen.MAP -> MapScreen(
                mapViewModel,
                Modifier
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
            )
            SelectedScreen.NOTIFICATIONS -> NotificationsScreen(notificationsViewModel)
            SelectedScreen.SETTINGS -> Text("Yet to implement")
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun HomeScreenNavigationBar(
    selectedScreen: MutableState<SelectedScreen>,
    numberOfNotifications: Int?
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedScreen.value == SelectedScreen.MAP,
            onClick = { selectedScreen.value = SelectedScreen.MAP },
            icon = {
                Icon(
                    Icons.Filled.Public,
                    contentDescription = stringResource(R.string.home_bottom_bar_map_description)
                )
            }
        )
        NavigationBarItem(
            selected = selectedScreen.value == SelectedScreen.NOTIFICATIONS,
            onClick = { selectedScreen.value = SelectedScreen.NOTIFICATIONS },
            icon = {
                if (numberOfNotifications != null) {
                    BadgedBox(badge = {
                        Badge { Text(numberOfNotifications.toString()) }
                    }) {
                        Icon(
                            Icons.Filled.Notifications,
                            contentDescription = stringResource(R.string.home_bottom_bar_notifications_description)
                        )
                    }
                } else {
                    Icon(
                        Icons.Filled.Notifications,
                        contentDescription = stringResource(R.string.home_bottom_bar_notifications_description)
                    )
                }
            }
        )
        NavigationBarItem(
            selected = selectedScreen.value == SelectedScreen.SETTINGS,
            onClick = { selectedScreen.value = SelectedScreen.SETTINGS },
            icon = {
                Icon(
                    Icons.Filled.Settings,
                    contentDescription = stringResource(R.string.home_bottom_bar_settings_description)
                )
            }
        )
    }
}