package com.example.smallworld.ui.map.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smallworld.R
import com.example.smallworld.data.profile.Profile
import com.example.smallworld.data.profile.models.FriendshipStatus
import com.example.smallworld.ui.theme.SmallWorldTheme

@Composable
fun ProfileComponent(
    profile: Profile,
    onSendRequestButtonClick: () -> Unit,
    onAcceptRequestButtonClick: () -> Unit,
    onDeclineRequesButtonClickt: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                text = profile.username,
                style = MaterialTheme.typography.titleMedium,
            )
            FriendshipButtons(
                profile.friendshipStatus,
                modifier = Modifier.padding(top = 5.dp),
                onSendRequestButtonClick = onSendRequestButtonClick,
                onAcceptRequestButtonClick = onAcceptRequestButtonClick,
                onDeclineRequesButtonClickt = onDeclineRequesButtonClickt
            )
        }
    }
}

@Composable
private fun FriendshipButtons(
    friendshipStatus: FriendshipStatus,
    onSendRequestButtonClick: () -> Unit,
    onAcceptRequestButtonClick: () -> Unit,
    onDeclineRequesButtonClickt: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (friendshipStatus) {
        FriendshipStatus.NOT_FRIENDS -> ProfileIconButton(
            modifier = modifier,
            imageVector = Icons.Filled.PersonAdd,
            text = stringResource(R.string.profile_add_friend_button_text),
            color = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            onClick = onSendRequestButtonClick
        )
        FriendshipStatus.OUTGOING_REQUEST -> ProfileIconButton(
            modifier = modifier,
            imageVector = Icons.Filled.Done,
            text = stringResource(R.string.profile_sent_request_button_text),
            color = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = null
        )
        FriendshipStatus.INCOMING_REQUEST -> Row(modifier) {
            ProfileIconButton(
                imageVector = Icons.Filled.Mail,
                text = stringResource(R.string.profile_accept_request_button_text),
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = onAcceptRequestButtonClick
            )
            ProfileIconButton(
                modifier = Modifier.padding(start = 8.dp),
                imageVector = Icons.Filled.Close,
                text = stringResource(R.string.profile_decline_request_button_text),
                color = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                onClick = onDeclineRequesButtonClickt
            )
        }
        FriendshipStatus.FRIENDS -> ProfileIconButton(
            modifier = modifier,
            imageVector = Icons.Filled.Group,
            text = stringResource(R.string.profile_confirmed_friends_button_text),
            color = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = null
        )
    }
}

@Composable
private fun ProfileIconButton(
    imageVector: ImageVector,
    text: String,
    color: Color,
    contentColor: Color,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(color)
            .run {
                if (onClick != null) clickable(onClick = onClick) else this
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(
                start = 9.dp, top = 4.dp, end = 15.dp, bottom = 4.dp
            )
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                val density = LocalDensity.current
                val iconSizeDp = remember(density) { with(density) { 20.sp.toDp() } }
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
}


@Preview(widthDp = 412)
@Composable
fun PreviewAddFriendButton() {
    SmallWorldTheme {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            FriendshipButtons(
                FriendshipStatus.NOT_FRIENDS,
                onAcceptRequestButtonClick = {},
                onSendRequestButtonClick = {},
                onDeclineRequesButtonClickt = {})
            FriendshipButtons(
                FriendshipStatus.OUTGOING_REQUEST,
                onAcceptRequestButtonClick = {},
                onSendRequestButtonClick = {},
                onDeclineRequesButtonClickt = {})
            FriendshipButtons(
                FriendshipStatus.INCOMING_REQUEST,
                onAcceptRequestButtonClick = {},
                onSendRequestButtonClick = {},
                onDeclineRequesButtonClickt = {})
            FriendshipButtons(
                FriendshipStatus.FRIENDS,
                onAcceptRequestButtonClick = {},
                onSendRequestButtonClick = {},
                onDeclineRequesButtonClickt = {})
        }
    }
}

@Preview(widthDp = 300)
@Composable
fun ProfilePreview() {
    ProfileComponent(
        Profile("", "jared", FriendshipStatus.NOT_FRIENDS),
        onAcceptRequestButtonClick = {},
        onSendRequestButtonClick = {},
        onDeclineRequesButtonClickt = {})
}