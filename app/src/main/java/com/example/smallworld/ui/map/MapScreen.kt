@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialApi::class)

package com.example.smallworld.ui.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smallworld.databinding.LayoutFragmentContainerBinding
import com.example.smallworld.ui.map.components.*
import kotlinx.coroutines.flow.collectLatest

private val searchBarHeight = 56.dp
private val searchBarPadding = 16.dp

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
        viewModel.onSheetVisibilityChanged(bottomSheetState.currentVisibility)
    }

    Box(
        modifier.fillMaxSize()
    ) {
        AndroidViewBinding(
            LayoutFragmentContainerBinding::inflate, modifier = Modifier.fillMaxSize()
        ) {
            fragmentContainerView.getFragment<MapFragment>().apply {
                setOnMapClickListener {
                    focusManager.clearFocus()
                    searchBarActive.value = false
                }
                setCompassMargins(
                    top = searchBarHeight + searchBarPadding * 2, right = searchBarPadding
                )
            }
        }
        MapSearchBar(
            query = state.value.query,
            onQueryChange = viewModel::onQueryChange,
            onSearch = { focusManager.clearFocus() },
            active = searchBarActive.value,
            onActiveChange = {
                searchBarActive.value = it
                // todo remove if unnecessary
                if (!searchBarActive.value) focusManager.clearFocus()
            },
            modifier = Modifier
                .imePadding()
                .padding(16.dp)
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