package com.example.smallworld.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smallworld.data.search.SearchRepository
import com.example.smallworld.data.search.models.User
import com.example.smallworld.services.NetworkService
import com.example.smallworld.ui.snackbar.SnackBarMessage
import com.example.smallworld.ui.snackbar.SnackBarMessageBus
import com.example.smallworld.util.logError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MapSearchResultsState {
    NO_QUERY,
    NO_RESULTS,
    RESULTS
}

sealed class MapScreenState(
    val query: String = "",
    val searchResults: List<User> = emptyList(),
    val searchResultsState: MapSearchResultsState = MapSearchResultsState.NO_QUERY,
    val profileBottomSheetVisibility: BottomSheetVisibility,
    val profileUsername: String,
    val profileFriendshipStatus: FriendshipStatus
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val networkService: NetworkService,
    private val snackBarMessageBus: SnackBarMessageBus
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
                if (networkService.isOnlineStateFlow.value) {
                    logError(e)
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

    private val _state = MutableStateFlow(MapScreenState())
    val state: StateFlow<MapScreenState> = _state

    init {
        viewModelScope.launch {
            combine(
                query,
                searchResults,
                searchResultsState
            ) { query, searchResults, searchResultsState ->
                MapScreenState(query, searchResults, searchResultsState)
            }.collect { _state.value = it }
        }
    }

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onSearchItemClick(id: User) {

    }

    fun onFriendButtonClick() {

    }

    fun onProfileDismiss() {

    }
}