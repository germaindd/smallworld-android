package com.example.smallworld.ui.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smallworld.R
import com.example.smallworld.ui.theme.SmallWorldTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    val refreshState = rememberPullRefreshState(state.refreshing, viewModel::refreshRequests)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(
                refreshState,
                enabled = state.notificationsResult !is NotificationsResult.Loading
            )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
        ) {
            when (state.notificationsResult) {
                NotificationsResult.Loading -> NotificationsLoadingState()
                NotificationsResult.Empty -> NotificationsEmptyState(
                    modifier = Modifier.verticalScroll(
                        rememberScrollState()
                    )
                )
                NotificationsResult.Error -> NotificationsErrorState(
                    modifier = Modifier.verticalScroll(
                        rememberScrollState()
                    )
                )
                is NotificationsResult.Loaded -> LazyColumn {
                    items(state.notificationsResult.friendRequests) { request ->
                        FriendRequestTile(
                            username = request.username,
                            onAcceptClick = { viewModel.acceptRequest(request) },
                            onDeclineClick = { viewModel.declineRequest(request) }
                        )
                        Divider()
                    }
                }
            }
            PullRefreshIndicator(
                state.refreshing,
                refreshState,
                Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun NotificationsLoadingState(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun NotificationsEmptyState(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Icon(
            Icons.Filled.PersonAdd,
            contentDescription = null,
            Modifier.size(128.dp),
            MaterialTheme.colorScheme.surfaceTint
        )
        Text(
            "No pending requests.",
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun NotificationsErrorState(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Icon(
            Icons.Filled.Error,
            contentDescription = null,
            Modifier.size(128.dp),
            MaterialTheme.colorScheme.surfaceTint
        )
        Text(
            stringResource(R.string.notifications_error_state_text),
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun FriendRequestTile(username: String, onAcceptClick: () -> Unit, onDeclineClick: () -> Unit) {
    Row(
        modifier = Modifier.padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Column(Modifier.padding(start = 8.dp)) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(username)
                    }
                    append(stringResource(R.string.notifications_sent_you_a_friend_request))
                },
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onAcceptClick, modifier = Modifier
                        .padding(top = 8.dp)
                        .weight(1f)
                ) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                    Text(
                        stringResource(R.string.notifications_accept_request_button_text),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Button(
                    onClick = onDeclineClick,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = null)
                    Text(
                        stringResource(R.string.notifications_decline_request_button_text),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Preview(widthDp = 412, backgroundColor = 0xFFFBFDF8)
@Composable
fun FriendRequestTilePreview() {
    SmallWorldTheme {
        FriendRequestTile("username", { }) { }
    }
}

@Preview(widthDp = 412, backgroundColor = 0xFFFBFDF8)
@Composable
fun NotificationsEmptyPreview() {
    SmallWorldTheme {
        NotificationsEmptyState()
    }
}

@Preview(widthDp = 412, backgroundColor = 0xFFFBFDF8)
@Composable
fun NotificationsLoadingPreview() {
    SmallWorldTheme {
        NotificationsLoadingState()
    }
}

@Preview(widthDp = 412, backgroundColor = 0xffffff)
@Composable
fun NotificationsErrorPreview() {
    SmallWorldTheme {
        NotificationsErrorState()
    }
}