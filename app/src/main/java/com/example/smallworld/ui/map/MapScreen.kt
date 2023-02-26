package com.example.smallworld.ui.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.*
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
import com.example.smallworld.ui.map.components.*
import kotlinx.coroutines.flow.collectLatest

private val searchBarHeight = 56.dp
private val searchBarPadding = 16.dp
private val currentLocationButtonHeight = 56.dp
private val currentLocationButtonPadding = 16.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel, modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    val searchBarActive = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val bottomSheetState =
        rememberBottomSheetState(initialValue = state.value.bottomSheetVisibility)

    // respond to viemodel show bottom sheet event
    LaunchedEffect(viewModel, bottomSheetState) {
        viewModel.moveBottomSheet.collectLatest {
            if (it != bottomSheetState.swipeableState.currentValue || it != bottomSheetState.swipeableState.targetValue) {
                when (it) {
                    BottomSheetVisibility.HIDDEN -> bottomSheetState.hide()
                    BottomSheetVisibility.SHOWING -> {
                        bottomSheetState.show()
                    }
                }
            }
        }
    }
    LaunchedEffect(bottomSheetState.currentVisibility) {
        viewModel
            .onSheetVisibilityChanged(bottomSheetState.currentVisibility)
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
                onClick = viewModel::onGoToCurrentLocation,
                Modifier
                    .padding(end = 16.dp)
                    .align(Alignment.End),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Icon(
                    imageVector = Icons.Filled.MyLocation,
                    contentDescription = stringResource(R.string.map_screen_current_location_button_content_description)
                )
            }
        }
        BottomSheet(
            modifier = Modifier.align(Alignment.BottomCenter), bottomSheetState = bottomSheetState
        ) {
            state.value.profile?.let {
                ProfileComponent(
                    it,
                    onSendRequestButtonClick = viewModel::sendRequest,
                    onDeclineRequesButtonClickt = viewModel::declineRequest,
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
        LayoutFragmentContainerBinding::inflate, modifier = Modifier.fillMaxSize()
    ) {
        fragmentContainerView.getFragment<MapFragment>().apply {
            setOnMapClickListener(onClick)
            setCompassMargins(
                top = compassMarginTop, right = compassMarginRight
            )
            setViewModelStoreOwner(
                viewModelStoreOwner ?: error("LocalViewModelStoreOwner not defined.")
            )
        }
    }
}