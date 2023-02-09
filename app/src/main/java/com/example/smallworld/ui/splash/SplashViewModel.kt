package com.example.smallworld.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smallworld.services.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val authService: AuthService) : ViewModel() {
    private val _onIsUserSignedIn = MutableSharedFlow<Boolean>(1)
    val onIsUserSignedIn: SharedFlow<Boolean> = _onIsUserSignedIn

    init {
        viewModelScope.launch {
            _onIsUserSignedIn.emit(authService.isLoggedIn())
        }
    }
}