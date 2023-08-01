package com.example.smallworld.ui.flows.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smallworld.data.auth.AuthRepository
import com.example.smallworld.data.auth.models.SignUpValidationResult
import com.example.smallworld.data.auth.AuthTokenStore
import com.example.smallworld.util.ConnectivityStatus
import com.example.smallworld.ui.components.snackbar.SnackBarMessage
import com.example.smallworld.ui.components.snackbar.SnackBarMessageBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authTokenStore: AuthTokenStore,
    private val snackBarMessageBus: SnackBarMessageBus,
    private val connectivityStatus: ConnectivityStatus
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
                val (emailResult, passwordResult) = try {
                    authRepository.validateEmailPassword(email.value, password.value)
                } catch (error: Throwable) {
                    checkIfOfflineAndTriggerSnackbar()
                    Timber.e(error)
                    return@launch
                }
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
                val usernameValidity = try {
                    authRepository.validateUsername(username.value)
                } catch (e: Throwable) {
                    Timber.e(e)
                    checkIfOfflineAndTriggerSnackbar()
                    return@launch
                }
                if (usernameValidity == SignUpValidationResult.SUCCESS) {
                    val tokens = try {
                        authRepository.signUp(username.value, password.value, email.value)
                    } catch (e: Throwable) {
                        Timber.e(e)
                        checkIfOfflineAndTriggerSnackbar()
                        return@launch
                    }
                    authTokenStore.setAccessTokens(tokens)
                    _onSignUpSuccess.emit(Unit)
                } else _usernameError.value = usernameValidity
            }
    }

    private suspend fun checkIfOfflineAndTriggerSnackbar() {
        snackBarMessageBus.sendMessage(
            if (connectivityStatus.isOnlineStateFlow.value)
                SnackBarMessage.SIGN_UP_ERROR_UNKNOWN
            else SnackBarMessage.NO_NETWORK
        )
    }
}