package com.example.smallworld.ui.flows.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smallworld.data.friends.FriendsRepository
import com.example.smallworld.data.friends.dto.FriendRequest
import com.example.smallworld.util.ConnectivityStatus
import com.example.smallworld.ui.components.snackbar.SnackBarMessage
import com.example.smallworld.ui.components.snackbar.SnackBarMessageBus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class NotificationsScreenState(
    val refreshing: Boolean = false,
    val notificationsResult: NotificationsResult = NotificationsResult.Loading,
    val numberOfNotifications: Int? = null
)

sealed class NotificationsResult {
    object Loading : NotificationsResult()
    object Empty : NotificationsResult()
    data class Loaded(
        val friendRequests: List<FriendRequest>
    ) : NotificationsResult()

    object Error : NotificationsResult()
}

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val friendsRepository: FriendsRepository,
    private val connectivityStatus: ConnectivityStatus,
    private val snackBarMessageBus: SnackBarMessageBus
) : ViewModel() {
    var firstVisibleItemOffset: Int = 0
    var firstVisibleItemIndex: Int = 0
    private val refreshing = MutableStateFlow(false)

    private val notificationsResult: MutableStateFlow<NotificationsResult> =
        MutableStateFlow(NotificationsResult.Loading)

    private val numberOfNotifications = notificationsResult
        .map { if (it is NotificationsResult.Loaded && it.friendRequests.isNotEmpty()) it.friendRequests.size + 1 else null }

    val state =
        combine(
            refreshing,
            notificationsResult,
            numberOfNotifications
        ) { refreshing, notificationsResult, numberOfNotifications ->
            NotificationsScreenState(refreshing, notificationsResult, numberOfNotifications)
        }.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            NotificationsScreenState(
                refreshing = false,
                notificationsResult = NotificationsResult.Loading,
                numberOfNotifications = null
            ),
        )

    init {
        viewModelScope.launch {
            fetchRequests()
        }
    }


    private suspend fun fetchRequests() {
        val requests = try {
            friendsRepository.getRequests()
        } catch (e: Throwable) {
            if (connectivityStatus.isOnlineStateFlow.value) {
                Timber.e(e)
                snackBarMessageBus.sendMessage(SnackBarMessage.ERROR_UNKNOWN)
            } else {
                snackBarMessageBus.sendMessage(SnackBarMessage.NO_NETWORK)
            }
            // if the user
            if (notificationsResult.value is NotificationsResult.Loading)
                notificationsResult.value = NotificationsResult.Error
            return
        }
        notificationsResult.value = if (requests.isEmpty()) NotificationsResult.Empty
        else NotificationsResult.Loaded(requests)
    }

    fun refreshRequests() {
        refreshing.value = true
        viewModelScope.launch {
            fetchRequests()
            refreshing.value = false
        }
    }

    fun acceptRequest(acceptingRequest: FriendRequest) {
        viewModelScope.launch {
            try {
                friendsRepository.acceptRequest(acceptingRequest.userId)
            } catch (e: Throwable) {
                if (connectivityStatus.isOnlineStateFlow.value) {
                    Timber.e(e)
                    snackBarMessageBus.sendMessage(SnackBarMessage.ERROR_UNKNOWN)
                } else {
                    snackBarMessageBus.sendMessage(SnackBarMessage.NO_NETWORK)
                }
                return@launch
            }
            (notificationsResult.value as? NotificationsResult.Loaded)?.let { loadedState ->
                val remainingRequests =
                    loadedState.friendRequests.filter { it.userId != acceptingRequest.userId }
                notificationsResult.value =
                    if (remainingRequests.isNotEmpty()) NotificationsResult.Loaded(
                        remainingRequests
                    )
                    else NotificationsResult.Empty
            }
        }
    }

    fun declineRequest(decliningRequest: FriendRequest) {
        viewModelScope.launch {
            try {
                friendsRepository.declineRequest(decliningRequest.userId)
            } catch (e: Throwable) {
                if (connectivityStatus.isOnlineStateFlow.value) {
                    Timber.e(e)
                    snackBarMessageBus.sendMessage(SnackBarMessage.ERROR_UNKNOWN)
                } else {
                    snackBarMessageBus.sendMessage(SnackBarMessage.NO_NETWORK)
                }
                return@launch
            }
            (notificationsResult.value as? NotificationsResult.Loaded)?.let { loadedState ->
                val remainingRequests =
                    loadedState.friendRequests.filter { it.userId != decliningRequest.userId }
                notificationsResult.value =
                    if (remainingRequests.isNotEmpty()) NotificationsResult.Loaded(
                        remainingRequests
                    ) else NotificationsResult.Empty
            }
        }
    }
}