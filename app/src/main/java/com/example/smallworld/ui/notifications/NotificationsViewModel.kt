package com.example.smallworld.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smallworld.data.friends.FriendsRepository
import com.example.smallworld.data.friends.dto.FriendRequest
import com.example.smallworld.services.NetworkService
import com.example.smallworld.ui.snackbar.SnackBarMessage
import com.example.smallworld.ui.snackbar.SnackBarMessageBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class NotificationsScreenState {
    object Loading : NotificationsScreenState()
    object Empty : NotificationsScreenState()
    data class Loaded(
        val friendRequests: List<FriendRequest>
    ) : NotificationsScreenState()

    object Error : NotificationsScreenState()
}

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val friendsRepository: FriendsRepository,
    private val networkService: NetworkService,
    private val snackBarMessageBus: SnackBarMessageBus
) : ViewModel() {
    private var prevState: NotificationsScreenState = NotificationsScreenState.Loading

    val state: MutableStateFlow<NotificationsScreenState> =
        MutableStateFlow(NotificationsScreenState.Loading)

    init {
        refreshRequests()
    }


    fun refreshRequests() {
        prevState = state.value
        state.value = NotificationsScreenState.Loading
        viewModelScope.launch {
            val requests = try {
                friendsRepository.getRequests()
            } catch (e: Throwable) {
                if (networkService.isOnlineStateFlow.value) {
                    Timber.e(e)
                    snackBarMessageBus.sendMessage(SnackBarMessage.ERROR_UNKNOWN)
                } else {
                    snackBarMessageBus.sendMessage(SnackBarMessage.NO_NETWORK)
                }
                state.value = when (prevState) {
                    is NotificationsScreenState.Loaded,
                    NotificationsScreenState.Empty -> prevState
                    NotificationsScreenState.Error,
                    NotificationsScreenState.Loading -> NotificationsScreenState.Error
                }
                return@launch
            }
            state.value = NotificationsScreenState.Loaded(requests)
        }
    }

    fun acceptRequest(acceptingRequest: FriendRequest) {
        viewModelScope.launch {
            try {
                friendsRepository.acceptRequest(acceptingRequest.userId)
            } catch (e: Throwable) {
                if (networkService.isOnlineStateFlow.value) {
                    Timber.e(e)
                    snackBarMessageBus.sendMessage(SnackBarMessage.ERROR_UNKNOWN)
                } else {
                    snackBarMessageBus.sendMessage(SnackBarMessage.NO_NETWORK)
                }
                return@launch
            }
            (state.value as? NotificationsScreenState.Loaded)
                ?.let { loadedState ->
                    state.value =
                        NotificationsScreenState.Loaded(loadedState.friendRequests.filter { it.userId != acceptingRequest.userId })
                }
        }
    }
}