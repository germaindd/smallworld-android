package com.example.smallworld.ui.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smallworld.ui.theme.SmallWorldTheme

@Composable
fun NotificationsScreen() {
}

@Composable
fun FriendRequestTile() {
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
                        append("username")
                    }
                    append(" sent you a friend request.")
                },
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { }, modifier = Modifier
                        .padding(top = 8.dp)
                        .weight(1f)
                ) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                    Text("Accept", modifier = Modifier.padding(start = 8.dp))
                }
                Button(
                    onClick = { },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = null)
                    Text("Decline", modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}

@Preview(widthDp = 412, backgroundColor = 0xffffff)
@Composable
fun FriendRequestTilePreview() {
    SmallWorldTheme {
        FriendRequestTile()
    }
}