package com.example.smallworld.ui.flows.map

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smallworld.data.friends.FriendsRepository
import com.example.smallworld.data.location.LocationRepository
import com.example.smallworld.data.location.model.Location
import com.example.smallworld.data.location.model.UpdateLocation
import com.example.smallworld.data.profile.ProfileRepository
import com.example.smallworld.data.profile.enums.FriendshipStatus
import com.example.smallworld.data.profile.model.Profile
import com.example.smallworld.data.search.SearchRepository
import com.example.smallworld.data.search.model.User
import com.example.smallworld.ui.components.snackbar.SnackBarMessage
import com.example.smallworld.ui.components.snackbar.SnackBarMessageBus
import com.example.smallworld.ui.flows.map.components.BottomSheetVisibility
import com.example.smallworld.util.ConnectivityStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

enum class MapSearchResultsState {
    NO_QUERY,
    NO_RESULTS,
    RESULTS
}

enum class CurrentLocationButtonState {
    GO_T0_LOCATION,
    UPDATE_LOCATION
}

data class MapScreenCameraState(
    val latitude: Double,
    val longitude: Double,
    val zoom: Double,
    val bearing: Double
)

data class MapScreenState(
    val query: String = "",
    val searchResults: List<User> = emptyList(),
    val searchResultsState: MapSearchResultsState = MapSearchResultsState.NO_QUERY,
    val bottomSheetVisibility: BottomSheetVisibility = BottomSheetVisibility.HIDDEN,
    val profile: Profile? = null,
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val connectivityStatus: ConnectivityStatus,
    private val snackBarMessageBus: SnackBarMessageBus,
    private val profileRepository: ProfileRepository,
    private val friendsRepository: FriendsRepository,
    private val permissionsManager: PermissionsManager,
    private val locationRepository: LocationRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {
    private val query: MutableStateFlow<String> = MutableStateFlow("")

    @OptIn(FlowPreview::class)
    private val debouncedQuery =
        query
            .debounce(100)
            .shareIn(viewModelScope, SharingStarted.Eagerly)

    private val searchResults: Flow<List<User>> = debouncedQuery
        .map {
            if (it.isBlank())
                emptyList()
            else try {
                searchRepository.search(it)
            } catch (e: Throwable) {
                if (connectivityStatus.isOnlineStateFlow.value) {
                    Timber.e(e)
                    snackBarMessageBus.sendMessage(SnackBarMessage.ERROR_UNKNOWN)
                } else {
                    snackBarMessageBus.sendMessage(SnackBarMessage.NO_NETWORK)
                }
                null
            }
        }
        .filterNotNull()
        .shareIn(viewModelScope, SharingStarted.Eagerly)

    private val searchResultsState: Flow<MapSearchResultsState> =
        debouncedQuery.zip(searchResults) { query, searchResults ->
            when {
                query.isBlank() -> MapSearchResultsState.NO_QUERY
                searchResults.isEmpty() -> MapSearchResultsState.NO_RESULTS
                else -> MapSearchResultsState.RESULTS
            }
        }

    private val bottomSheetVisibility = MutableStateFlow(BottomSheetVisibility.HIDDEN)
    private val profile = MutableStateFlow<Profile?>(null)

    private val _moveBottomSheet = MutableSharedFlow<BottomSheetVisibility>()
    val moveBottomSheet: SharedFlow<BottomSheetVisibility> = _moveBottomSheet

    private val _friendsLocations = MutableStateFlow<List<Location>>(emptyList())
    val friendsLocations: StateFlow<List<Location>> = _friendsLocations

    private val _isLocationEnabled = MutableStateFlow(false)
    val isLocationEnabled: StateFlow<Boolean> = _isLocationEnabled

    private val _isTrackingLocation = MutableStateFlow(false)
    val isTrackingLocation: StateFlow<Boolean> = _isTrackingLocation

    val currentLocationButtonState =
        isTrackingLocation.map { if (it) CurrentLocationButtonState.UPDATE_LOCATION else CurrentLocationButtonState.GO_T0_LOCATION }
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                CurrentLocationButtonState.GO_T0_LOCATION
            )

    private val _onRequestLocationPermissions = Channel<Unit>()
    val onRequestLocationPermissions: ReceiveChannel<Unit> = _onRequestLocationPermissions

    private val _goToCurrentLocation = MutableSharedFlow<Unit>()
    val goToCurrentLocation: SharedFlow<Unit> = _goToCurrentLocation

    private val _flyIntoCurrentLocation = Channel<Unit>()
    val flyIntoCurrentLocation: ReceiveChannel<Unit> = _flyIntoCurrentLocation

    private val _goToLocation = MutableSharedFlow<Location>()
    val goToLocation: SharedFlow<Location> = _goToLocation

    private val _cameraState: MutableStateFlow<MapScreenCameraState?> = MutableStateFlow(null)
    val cameraState: StateFlow<MapScreenCameraState?> = _cameraState

    val state: StateFlow<MapScreenState> = combine(
        query,
        searchResults,
        searchResultsState,
        bottomSheetVisibility,
        profile
    ) { query, searchResults, searchResultsState, bottomSheetVisibility, profile ->
        MapScreenState(
            query = query,
            searchResults = searchResults,
            searchResultsState = searchResultsState,
            bottomSheetVisibility = bottomSheetVisibility,
            profile = profile
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, MapScreenState())

    init {
        viewModelScope.launch {
            fetchFriendsLocations()
            if (permissionsManager.hasLocationPermissions) {
                _isLocationEnabled.value = true
                _flyIntoCurrentLocation.send(Unit)
            } else _onRequestLocationPermissions.send(Unit)
        }
    }

    private fun fetchFriendsLocations() {
        viewModelScope.launch {
            _friendsLocations.value = locationRepository.getFriendsLocations()
        }
    }

    fun onLocationPermissionsResult(havePermissions: Boolean) {
        viewModelScope.launch {
            _isLocationEnabled.value = havePermissions
            if (havePermissions) _flyIntoCurrentLocation.send(Unit)
        }
    }

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onSearchItemClick(user: User) {
        viewModelScope.launch {
            val profile = goToUserProfile(user.id)
            if (profile?.friendshipStatus == FriendshipStatus.FRIENDS)
                friendsLocations.value.firstOrNull {
                    it.userId == user.id
                }?.let { location ->
                    _goToLocation.emit(location)
                } ?: snackBarMessageBus.sendMessage(
                    SnackBarMessage.MAP_SCREEN_COULD_NOT_FIND_FRIENDS_LOCATION
                )
        }
    }

    fun onFriendLocationClick(location: Location) {
        viewModelScope.launch {
            goToUserProfile(location.userId)
            _goToLocation.emit(location)
        }
    }

    private suspend fun goToUserProfile(userId: String): Profile? {
        val profileResponse = try {
            profileRepository.getProfile(userId)
        } catch (e: Throwable) {
            if (connectivityStatus.isOnlineStateFlow.value) {
                Timber.e(e)
                snackBarMessageBus.sendMessage(SnackBarMessage.ERROR_UNKNOWN)
            } else {
                snackBarMessageBus.sendMessage(SnackBarMessage.NO_NETWORK)
            }
            return null
        }

        _moveBottomSheet.emit(BottomSheetVisibility.SHOWING)
        profile.value = profileResponse
        return profileResponse
    }

    @SuppressLint("MissingPermission") // button would not be visible if user had not already granted permission
    fun onCurrentLocationClick() {
        viewModelScope.launch {
            when (currentLocationButtonState.value) {
                CurrentLocationButtonState.GO_T0_LOCATION -> _goToCurrentLocation.emit(Unit)
                CurrentLocationButtonState.UPDATE_LOCATION -> {
                    locationProvider.getCurrentLocation().let {
                        try {
                            locationRepository.updateLocation(
                                UpdateLocation(it.latitude, it.longitude)
                            )
                        } catch (e: Throwable) {
                            if (connectivityStatus.isOnlineStateFlow.value) {
                                Timber.e(e)
                                snackBarMessageBus.sendMessage(SnackBarMessage.ERROR_UNKNOWN)
                            } else {
                                snackBarMessageBus.sendMessage(SnackBarMessage.NO_NETWORK)
                            }
                            return@launch
                        }
                    }
                }
            }
        }
    }

    fun sendRequest() {
        val userId = profile.value?.userId ?: return
        viewModelScope.launch {
            try {
                friendsRepository.sendRequest(userId)
                profile.value =
                    profile.value?.copy(friendshipStatus = FriendshipStatus.OUTGOING_REQUEST)
            } catch (e: Throwable) {
                if (connectivityStatus.isOnlineStateFlow.value) {
                    Timber.e(e)
                    snackBarMessageBus.sendMessage(SnackBarMessage.ERROR_UNKNOWN)
                } else {
                    snackBarMessageBus.sendMessage(SnackBarMessage.NO_NETWORK)
                }
                return@launch
            }
        }
    }

    fun acceptRequest() {
        val userId = profile.value?.userId ?: return
        viewModelScope.launch {
            try {
                friendsRepository.acceptRequest(userId)
            } catch (e: Throwable) {
                if (connectivityStatus.isOnlineStateFlow.value) {
                    Timber.e(e)
                    snackBarMessageBus.sendMessage(SnackBarMessage.ERROR_UNKNOWN)
                } else {
                    snackBarMessageBus.sendMessage(SnackBarMessage.NO_NETWORK)
                }
                return@launch
            }
            profile.value?.let {
                profile.value = it.copy(friendshipStatus = FriendshipStatus.FRIENDS)
            }
        }
    }

    fun declineRequest() {
        val userId = profile.value?.userId ?: return
        viewModelScope.launch {
            try {
                friendsRepository.declineRequest(userId)
            } catch (e: Throwable) {
                if (connectivityStatus.isOnlineStateFlow.value) {
                    Timber.e(e)
                    snackBarMessageBus.sendMessage(SnackBarMessage.ERROR_UNKNOWN)
                } else {
                    snackBarMessageBus.sendMessage(SnackBarMessage.NO_NETWORK)
                }
                return@launch
            }
            profile.value?.let {
                profile.value = it.copy(friendshipStatus = FriendshipStatus.NOT_FRIENDS)
            }
        }
    }

    fun onSheetVisibilityChanged(visibility: BottomSheetVisibility) {
        bottomSheetVisibility.value = visibility
    }

    fun saveCameraPosition(latitude: Double, longitude: Double, zoom: Double, bearing: Double) {
        _cameraState.value = MapScreenCameraState(
            latitude = latitude,
            longitude = longitude,
            zoom = zoom,
            bearing = bearing
        )
    }

    fun setTrackLocation(track: Boolean) {
        _isTrackingLocation.value = track
    }
}