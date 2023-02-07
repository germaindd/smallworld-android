package com.example.smallworld.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
/**
* Shorthand to get a viewModel scoped to a particular navigation route. Useful when you need to get a viewmodel.
* Useful when you want to get a ViewModel scoped to a previous destination on the backstack or a parent nav
 * graph. The NavBackStackEntry used as the receiver itself is needed only because a NavBackStackEntry
 * is needed as a key to remember() when you use the function NavController.getBackStackEntry(route: String)
 * inside it or compose will trigger a compilation error.
*/
@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.getViewModelScopedTo(
    scopeRoute: String,
    navController: NavController
): T {
    val backStackEntry = remember(this) {
        navController.getBackStackEntry(scopeRoute)
    }
    return hiltViewModel(backStackEntry)
}