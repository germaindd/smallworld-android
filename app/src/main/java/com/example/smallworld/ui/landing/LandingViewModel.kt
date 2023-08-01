package com.example.smallworld.ui.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smallworld.util.ConnectivityStatus
import com.example.smallworld.ui.snackbar.SnackBarMessage
import com.example.smallworld.ui.snackbar.SnackBarMessageBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val connectivityStatus: ConnectivityStatus,
    private val snackBarMessageBus: SnackBarMessageBus
) : ViewModel() {
    private val _onNavigateToSignIn = MutableSharedFlow<Unit>()
    val onNavigateToSignIn: SharedFlow<Unit> = _onNavigateToSignIn

    private val _onNavigateToSignUp = MutableSharedFlow<Unit>()
    val onNavigateToSignUp: SharedFlow<Unit> = _onNavigateToSignUp

    private fun doIfOnline(operation: suspend () -> Unit) {
        viewModelScope.launch {
            if (connectivityStatus.isOnlineStateFlow.value) {
                operation()
            } else {
                snackBarMessageBus.sendMessage(SnackBarMessage.NO_NETWORK)
            }
        }
    }

    fun onSignInClick() {
        doIfOnline { _onNavigateToSignIn.emit(Unit) }
    }

    fun onSignUpClick() {
        doIfOnline { _onNavigateToSignUp.emit(Unit) }
    }
}