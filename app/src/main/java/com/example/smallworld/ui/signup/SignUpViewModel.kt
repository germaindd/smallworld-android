package com.example.smallworld.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smallworld.data.auth.AuthRepository
import com.example.smallworld.data.auth.models.SignUpValidationResult
import com.example.smallworld.services.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authService: AuthService
) : ViewModel() {

    // Screen One
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _emailError = MutableStateFlow<SignUpValidationResult?>(null)
    val emailError: StateFlow<SignUpValidationResult?> = _emailError

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _passwordError = MutableStateFlow(false)
    val passwordError: StateFlow<Boolean> = _passwordError

    private var currentValidateEmailPasswordJob: Job? = null

    private val _onScreenOneSuccess = MutableSharedFlow<Unit>()
    val onScreenOneSuccess: SharedFlow<Unit> = _onScreenOneSuccess

    fun onEmailChange(value: String) {
        _email.value = value
        _emailError.value = null
    }

    fun onPasswordChange(value: String) {
        _password.value = value
        _passwordError.value = false
    }

    fun onScreenOneSubmit() {
        val currentJob = currentValidateEmailPasswordJob
        if (
            ((currentJob == null || !currentJob.isActive)
                    && emailError.value == null) && !passwordError.value
        )
            currentValidateEmailPasswordJob = viewModelScope.launch {
                authRepository.validateEmailPassword(email.value, password.value)
                    .let { (emailResult, passwordResult) ->
                        if (
                            emailResult == SignUpValidationResult.SUCCESS
                            && passwordResult == SignUpValidationResult.SUCCESS
                        ) _onScreenOneSuccess.emit(Unit) else {
                            _emailError.value =
                                if (emailResult == SignUpValidationResult.SUCCESS) null else emailResult
                            _passwordError.value = passwordResult != SignUpValidationResult.SUCCESS
                        }
                    }
            }
    }

    // Screen Two
    private val _username = MutableStateFlow<String>("")
    val username: StateFlow<String> = _username

    private val _usernameError = MutableStateFlow<SignUpValidationResult?>(null)
    val usernameError: StateFlow<SignUpValidationResult?> = _usernameError

    var currentSignUpJob: Job? = null

    private val _onSignUpSuccess = MutableSharedFlow<Unit>()
    val onSignUpSuccess: SharedFlow<Unit> = _onSignUpSuccess

    fun onUsernameChange(value: String) {
        _username.value = value
        _usernameError.value = null
    }

    fun onScreenTwoSubmit() {
        val currentJob = currentSignUpJob
        if (
            (currentJob == null || !currentJob.isActive)
            && usernameError.value == null
        )
            currentSignUpJob = viewModelScope.launch {
                val usernameValidity = authRepository.validateUsername(username.value)
                if (usernameValidity == SignUpValidationResult.SUCCESS) {
                    val signUpDto =
                        authRepository.signUp(username.value, password.value, email.value)
                    authService.setAccessToken(signUpDto.accessToken)
                    _onSignUpSuccess.emit(Unit)
                } else _usernameError.value = usernameValidity
            }
    }
}