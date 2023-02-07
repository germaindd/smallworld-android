package com.example.smallworld.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smallworld.data.auth.AuthRepository
import com.example.smallworld.data.auth.models.SignUpValidationResult
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
    private val authRepository: AuthRepository
) : ViewModel() {
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

    fun onNextClick() {
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
                        ) _onScreenOneSuccess.emit(Unit)
                        _emailError.value =
                            if (emailResult == SignUpValidationResult.SUCCESS) null else emailResult
                        _passwordError.value = passwordResult != SignUpValidationResult.SUCCESS
                    }
            }
    }
}