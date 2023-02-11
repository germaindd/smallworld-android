package com.example.smallworld.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smallworld.data.auth.AuthRepository
import com.example.smallworld.services.AuthService
import com.example.smallworld.services.NetworkService
import com.example.smallworld.ui.snackbar.SnackBarMessage
import com.example.smallworld.ui.snackbar.SnackBarMessageBus
import com.example.smallworld.util.logError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.HttpURLConnection
import javax.inject.Inject

data class SignInScreenState(
    val usernameOrEmail: String = "",
    val password: String = ""
)

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val authService: AuthService,
    private val snackBarMessageBus: SnackBarMessageBus,
    private val networkService: NetworkService
) : ViewModel() {
    private val usernameOrEmail: MutableStateFlow<String> = MutableStateFlow("")
    private val password: MutableStateFlow<String> = MutableStateFlow("")

    private val _state = MutableStateFlow(SignInScreenState())
    val state: StateFlow<SignInScreenState> = _state

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
            } catch (e: HttpException) {
                logError(e)
                val message = when (e.code()) {
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    HttpURLConnection.HTTP_UNAUTHORIZED -> SnackBarMessage.SIGN_IN_ERROR_UNAUTHORIZED
                    else -> SnackBarMessage.SIGN_IN_ERROR_UNKNOWN
                }
                snackBarMessageBus.sendMessage(message)
            } catch (e: Throwable) {
                logError(e)
                snackBarMessageBus.sendMessage(
                    if (!networkService.isOnlineStateFlow.value)
                        SnackBarMessage.NO_NETWORK
                    else SnackBarMessage.SIGN_IN_ERROR_UNKNOWN
                )
            }
        }
    }
}