package com.example.smallworld.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    onFriendshipButtonClick: () -> Unit,
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
            FriendshipButton(
                profile.friendshipStatus,
                modifier = Modifier.padding(top = 5.dp),
                onClick = onFriendshipButtonClick
            )
        }
    }
}

@Composable
private fun FriendshipButton(
    friendshipStatus: FriendshipStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
private fun ProfileIconButton(
    imageVector: ImageVector,
    text: String,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(color)
            .run {
                if (enabled) clickable(onClick = onClick) else this
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
            FriendshipButton(FriendshipStatus.NOT_FRIENDS, onClick = {})
            FriendshipButton(FriendshipStatus.OUTGOING_REQUEST, onClick = {})
            FriendshipButton(FriendshipStatus.INCOMING_REQUEST, onClick = {})
            FriendshipButton(FriendshipStatus.FRIENDS, onClick = {})
        }
    }
}

@Preview(widthDp = 300)
@Composable
fun ProfilePreview() {
    ProfileComponent(
        Profile("", "jared", FriendshipStatus.NOT_FRIENDS),
        onFriendshipButtonClick = {})
}