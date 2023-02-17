package com.example.smallworld.ui.map

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smallworld.databinding.LayoutFragmentContainerBinding
import kotlinx.coroutines.delay

/**
 * Current requirement:
 * - make the maximum go down to the keyboard
 * -
 *
 * I could try to
 * - set a timer to see if the window insets change when i
 */
private val searchBarHeight = 56.dp
private val searchBarPadding = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel, modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val active = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val bottomSheetState =
        rememberBottomSheetState(initialValue = ProfileBottomSheetVisibility.HIDDEN)
    LaunchedEffect(bottomSheetState) {
        repeat(10) {
            delay(2000)
            bottomSheetState.show()
            delay(2000)
            bottomSheetState.hide()
        }
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
                    active.value = false
                    viewModel.onQueryChange("")
                }
                setCompassMargins(
                    top = searchBarHeight + searchBarPadding * 2, right = searchBarPadding
                )
            }
        }
        DockedSearchBar(query = state.value.query,
            onQueryChange = viewModel::onQueryChange,
            onSearch = { focusManager.clearFocus() },
            active = active.value,
            onActiveChange = {
                active.value = it
                if (!active.value) focusManager.clearFocus()
            },
            modifier = Modifier
                .imePadding()
                .padding(16.dp)
                .fillMaxWidth()
                .shadow(
                    2.dp,
                    shape = SearchBarDefaults.dockedShape // same shape as the one implemented by the component
                ),
            placeholder = { Text("Search") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search, contentDescription = null
                )
            }) {
            when (state.value.searchResultsState) {
                MapSearchResultsState.NO_QUERY -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = null,
                            Modifier.size(128.dp),
                            MaterialTheme.colorScheme.surfaceTint
                        )
                        Text(
                            "Type something in!",
                            modifier = Modifier.padding(top = 16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                MapSearchResultsState.NO_RESULTS -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Filled.SentimentDissatisfied,
                            contentDescription = null,
                            Modifier.size(128.dp),
                            MaterialTheme.colorScheme.surfaceTint
                        )
                        Text(
                            "No results found for \"${state.value.query}\"",
                            modifier = Modifier.padding(top = 16.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                MapSearchResultsState.RESULTS -> LazyColumn {
                    itemsIndexed(state.value.searchResults) { index, user ->
                        if (index != 0) Divider()
                        SearchItem(text = user.username) {}
                    }
                }
            }
        }
        BottomSheet(
            modifier = Modifier.align(Alignment.BottomCenter), bottomSheetState = bottomSheetState
        ) { Profile("jared") }
    }
}

@Composable
fun Profile(username: String, modifier: Modifier = Modifier) {
    Row(
        modifier
            .padding(horizontal = 16.dp)
            .height(IntrinsicSize.Min)
    ) {
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(64.dp)
        )
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxHeight(),
        ) {
            Text(
                text = username,
                style = MaterialTheme.typography.titleSmall,
            )
            AddFriendButton(modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun AddFriendButton(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.clickable { },
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.primary
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(
                start = 6.dp, top = 3.dp, end = 10.dp, bottom = 3.dp
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Text(
                "Add Friend",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Preview(widthDp = 300)
@Composable
fun ProfilePreview() {
    Profile(username = "jared")
}

@Composable
fun SearchItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(32.dp)
        )
        Text(text = text, modifier = Modifier.padding(start = 8.dp))
    }
}

@Preview(widthDp = 300)
@Composable
fun SearchItemPreview() {
    SearchItem("jared") {}
}