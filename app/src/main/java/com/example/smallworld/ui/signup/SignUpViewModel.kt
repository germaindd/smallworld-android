package com.example.smallworld.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smallworld.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private var _username = MutableStateFlow("")
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
        viewModelScope.launch {
            authRepository.signUp(username.value, password.value)
        }
    }
}