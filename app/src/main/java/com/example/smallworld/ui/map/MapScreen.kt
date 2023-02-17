package com.example.smallworld.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smallworld.R
import com.example.smallworld.databinding.LayoutFragmentContainerBinding
import com.example.smallworld.ui.theme.SmallWorldTheme

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
            modifier = Modifier.size(80.dp)
        )
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(start = 10.dp)
                .fillMaxHeight(),
        ) {
            Text(
                text = username,
                style = MaterialTheme.typography.titleMedium,
            )
            FriendshipButton(FriendshipStatus.FRIENDS, modifier = Modifier.padding(top = 5.dp))
        }
    }
}

enum class FriendshipStatus {
    NOT_FRIENDS,
    OUTGOING_REQUEST,
    INCOMING_REQUEST,
    FRIENDS
}

@Composable
private fun FriendshipButton(friendshipStatus: FriendshipStatus, modifier: Modifier = Modifier) {
    ProfileIconButton(
        imageVector = when (friendshipStatus) {
            FriendshipStatus.NOT_FRIENDS -> Icons.Filled.PersonAdd
            FriendshipStatus.OUTGOING_REQUEST -> Icons.Filled.Done
            FriendshipStatus.INCOMING_REQUEST -> Icons.Filled.Mail
            FriendshipStatus.FRIENDS -> Icons.Filled.Group
        },
        text = stringResource(
            when (friendshipStatus) {
                FriendshipStatus.NOT_FRIENDS -> R.string.profile_add_friend_button_text
                FriendshipStatus.OUTGOING_REQUEST -> R.string.profile_sent_request_button_text
                FriendshipStatus.INCOMING_REQUEST -> R.string.profile_accept_request_button_text
                FriendshipStatus.FRIENDS -> R.string.profile_confirmed_friends_button_text
            }
        ),
        color = when (friendshipStatus) {
            FriendshipStatus.NOT_FRIENDS,
            FriendshipStatus.INCOMING_REQUEST -> MaterialTheme.colorScheme.primary
            FriendshipStatus.OUTGOING_REQUEST,
            FriendshipStatus.FRIENDS -> MaterialTheme.colorScheme.surfaceVariant
        },
        enabled = when (friendshipStatus) {
            FriendshipStatus.NOT_FRIENDS -> true
            FriendshipStatus.OUTGOING_REQUEST,
            FriendshipStatus.INCOMING_REQUEST,
            FriendshipStatus.FRIENDS -> false
        },
        modifier = modifier
    )
}

@Composable
private fun ProfileIconButton(
    imageVector: ImageVector,
    text: String,
    color: Color,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(color)
            .run {
                if (enabled) clickable {} else this
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(
                start = 9.dp, top = 4.dp, end = 15.dp, bottom = 4.dp
            )
        ) {
            val iconSizeDp = with(LocalDensity.current) { 20.sp.toDp() }
            Icon(
                imageVector = imageVector,
                contentDescription = null,
                modifier = Modifier.size(iconSizeDp)
            )
            Text(
                text,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}


@Preview(widthDp = 300)
@Composable
fun PreviewAddFriendButton() {
    SmallWorldTheme {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            FriendshipButton(FriendshipStatus.NOT_FRIENDS)
            FriendshipButton(FriendshipStatus.OUTGOING_REQUEST)
            FriendshipButton(FriendshipStatus.INCOMING_REQUEST)
            FriendshipButton(FriendshipStatus.FRIENDS)
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