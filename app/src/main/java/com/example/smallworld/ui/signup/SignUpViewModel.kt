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

    private var _email = MutableStateFlow("")
    val email: StateFlow<String>
        get() = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String>
        get() = _password

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun onNextClick() {
        viewModelScope.launch {
            authRepository.signUp(email.value, password.value)
        }
    }
}