package com.example.smallworld.ui.flows.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smallworld.data.auth.AuthTokenStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val authTokenStore: AuthTokenStore) : ViewModel() {
    private val _onIsUserSignedIn = MutableSharedFlow<Boolean>(1)
    val onIsUserSignedIn: SharedFlow<Boolean> = _onIsUserSignedIn

    init {
        viewModelScope.launch {
            _onIsUserSignedIn.emit(authTokenStore.getAccessToken() != null)
        }
    }
}