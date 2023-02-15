package com.example.smallworld.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.example.smallworld.databinding.LayoutFragmentContainerBinding

/**
 * Current requirement:
 * - make the maximum go down to the keyboard
 * -
 *
 * I could try to
 * - set a timer to see if the window insets change when i
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    scaffoldPaddingValues: PaddingValues = PaddingValues(0.dp)
) {
    val query = remember {
        mutableStateOf("")
    }
    val active = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    Box(
        modifier
            .fillMaxSize()
    ) {
        AndroidViewBinding(
            LayoutFragmentContainerBinding::inflate,
            modifier = Modifier
                .fillMaxSize()
        ) {
            fragmentContainerView.getFragment<MapFragment>().setOnMapClickListener {
                focusManager.clearFocus()
                active.value = false
            }
        }
        DockedSearchBar(query = query.value,
            onQueryChange = { query.value = it },
            onSearch = { focusManager.clearFocus() },
            active = active.value,
            onActiveChange = {
                active.value = it
                if (!active.value) focusManager.clearFocus()
            },
            modifier = Modifier
                .windowInsetsPadding(
                    WindowInsets.ime.exclude(
                        WindowInsets(bottom = scaffoldPaddingValues.calculateBottomPadding())
                    )
                )
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
            repeat(30) {
                if (it != 0) Divider()
                SearchItem(text = "jared1") {

                }
            }
        }
    }
}

@Composable
fun SearchItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surface)
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