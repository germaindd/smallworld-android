package com.example.smallworld.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smallworld.data.HttpStatus
import com.example.smallworld.data.auth.AuthRepository
import com.example.smallworld.services.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

data class SignInScreenState(
    val usernameOrEmail: String = "",
    val password: String = ""
)

enum class SignInError {
    INVALID_CREDENTIALS,
    UNKNOWN
}

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authService: AuthService
) : ViewModel() {
    private val usernameOrEmail: MutableStateFlow<String> = MutableStateFlow("")
    private val password: MutableStateFlow<String> = MutableStateFlow("")

    private val _state = MutableStateFlow(SignInScreenState())
    val state: StateFlow<SignInScreenState> = _state

    private val _onSignInError = MutableSharedFlow<SignInError>()
    val onSignInError: SharedFlow<SignInError> = _onSignInError

    private val _onSignInSuccess = MutableSharedFlow<Unit>()
    val onSignInSuccess: SharedFlow<Unit> = _onSignInSuccess

    init {
        viewModelScope.launch {
            combine(usernameOrEmail, password) { usernameOrEmail, password ->
                SignInScreenState(usernameOrEmail, password)
            }.collect { _state.value = it }
        }
    }

    fun onUsernameOrEmailChange(value: String) {
        usernameOrEmail.value = value
    }

    fun onPasswordChange(value: String) {
        password.value = value
    }

    fun onSubmit() {
        viewModelScope.launch {
            try {
                val accessToken =
                    authRepository.signIn(usernameOrEmail.value, password.value).accessToken
                authService.setAccessToken(accessToken)
                _onSignInSuccess.emit(Unit)
            } catch (error: HttpException) {
                val signInError = when (error.code()) {
                    HttpStatus.BAD_REQUEST.code,
                    HttpStatus.UNAUTHORIZED.code -> SignInError.INVALID_CREDENTIALS
                    else -> SignInError.UNKNOWN
                }
                _onSignInError.emit(signInError)
            }
        }
    }
}