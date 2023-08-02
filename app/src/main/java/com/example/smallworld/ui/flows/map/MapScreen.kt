package com.example.smallworld.ui.flows.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.example.smallworld.R
import com.example.smallworld.databinding.LayoutFragmentContainerBinding
import com.example.smallworld.ui.flows.map.components.BottomSheet
import com.example.smallworld.ui.flows.map.components.BottomSheetVisibility
import com.example.smallworld.ui.flows.map.components.MapFragment
import com.example.smallworld.ui.flows.map.components.MapSearchBar
import com.example.smallworld.ui.flows.map.components.MapSearchResults
import com.example.smallworld.ui.flows.map.components.ProfileComponent
import com.example.smallworld.ui.flows.map.components.rememberBottomSheetState
import kotlinx.coroutines.flow.collectLatest

private val searchBarHeight = 56.dp
private val searchBarPadding = 16.dp
private val currentLocationButtonHeight = 56.dp
private val currentLocationButtonPadding = 16.dp

@Composable
fun MapScreen(
    viewModel: MapViewModel, modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val currentLocationButtonState =
        viewModel.currentLocationButtonState.collectAsStateWithLifecycle()

    val searchBarActive = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val bottomSheetState =
        rememberBottomSheetState(initialValue = state.value.bottomSheetVisibility)

    LaunchedEffect(viewModel, bottomSheetState) {
        viewModel.moveBottomSheet.collectLatest { toState ->
            if (
            // it's not at the target state
                toState != bottomSheetState.currentVisibility
                // or it is, but it's animating towards a different state
                || toState != bottomSheetState.targetVisibility
            ) {
                when (toState) {
                    BottomSheetVisibility.HIDDEN -> bottomSheetState.hide()
                    BottomSheetVisibility.SHOWING -> bottomSheetState.show()
                }
            }
        }
    }
    LaunchedEffect(bottomSheetState.currentVisibility) {
        viewModel.onSheetVisibilityChanged(bottomSheetState.currentVisibility)
    }

    Box(
        modifier.fillMaxSize()
    ) {
        MapComponent(
            onClick = {
                focusManager.clearFocus()
                searchBarActive.value = false
            },
            compassMarginTop = searchBarHeight + searchBarPadding * 2 + currentLocationButtonHeight + currentLocationButtonPadding,
            compassMarginRight = searchBarPadding
        )
        Column {
            MapSearchBar(
                query = state.value.query,
                onQueryChange = viewModel::onQueryChange,
                onSearch = { focusManager.clearFocus() },
                active = searchBarActive.value,
                onActiveChange = {
                    searchBarActive.value = it
                },
                modifier = Modifier
                    .imePadding()
                    .padding(searchBarPadding)
                    .fillMaxWidth()
                    .shadow(
                        2.dp,
                        shape = SearchBarDefaults.dockedShape // same shape as the one implemented by the component
                    )
            ) {
                MapSearchResults(
                    searchResultsState = state.value.searchResultsState,
                    query = state.value.query,
                    searchResults = state.value.searchResults,
                    onSearchItemClick = { user ->
                        focusManager.clearFocus()
                        searchBarActive.value = false
                        viewModel.onSearchItemClick(user)
                    }
                )
            }
            FloatingActionButton(
                onClick = viewModel::onCurrentLocationClick,
                Modifier
                    .padding(end = 16.dp)
                    .align(Alignment.End),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                when (currentLocationButtonState.value) {
                    CurrentLocationButtonState.GO_T0_LOCATION -> Icon(
                        imageVector = Icons.Filled.MyLocation,
                        contentDescription = stringResource(R.string.map_screen_current_location_button_content_description)
                    )

                    CurrentLocationButtonState.UPDATE_LOCATION -> Icon(
                        imageVector = Icons.Filled.Sync,
                        contentDescription = stringResource(R.string.map_screen_update_location_button_content_description)
                    )
                }
            }
        }
        BottomSheet(
            modifier = Modifier.align(Alignment.BottomCenter), bottomSheetState = bottomSheetState
        ) {
            state.value.profile?.let {
                ProfileComponent(
                    it,
                    onSendRequestButtonClick = viewModel::sendRequest,
                    onDeclineRequestButtonClick = viewModel::declineRequest,
                    onAcceptRequestButtonClick = viewModel::acceptRequest
                )
            }
        }
    }
}

@Composable
private fun MapComponent(
    onClick: () -> Unit,
    compassMarginTop: Dp,
    compassMarginRight: Dp
) {
    val viewModelStoreOwner = LocalViewModelStoreOwner.current
    AndroidViewBinding(
        { inflater, parent, attachToParent ->
            val binding = LayoutFragmentContainerBinding.inflate(inflater, parent, attachToParent)
            binding.fragmentContainerView.getFragment<MapFragment>().apply {
                setOnMapClickListener(onClick)
                setCompassMargins(top = compassMarginTop, right = compassMarginRight)
                setViewModelStoreOwner(
                    viewModelStoreOwner ?: error("LocalViewModelStoreOwner not defined.")
                )
            }
            binding
        },
        modifier = Modifier.fillMaxSize()
    ) {}
}