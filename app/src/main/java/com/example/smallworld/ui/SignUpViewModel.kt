package com.example.smallworld.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {

    private val _username = MutableStateFlow("")
    val username: StateFlow<String>
        get() = _username

    private val _password = MutableStateFlow("")
    val password: StateFlow<String>
        get() = _password

    fun onUsernameChange(value: String) {
        _username.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun onSignUpClick() {

    }
}