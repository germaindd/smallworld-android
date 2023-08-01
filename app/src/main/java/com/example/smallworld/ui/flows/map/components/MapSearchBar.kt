@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.smallworld.ui.flows.map.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.Divider
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smallworld.R
import com.example.smallworld.data.search.model.User
import com.example.smallworld.ui.flows.map.MapSearchResultsState

@Composable
fun MapSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    DockedSearchBar(query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        active = active,
        onActiveChange = onActiveChange,
        modifier = modifier,
        placeholder = { Text("Search") },
        leadingIcon = { Icon(imageVector = Icons.Filled.Search, contentDescription = null) })
    {
        content()
    }
}

@Composable
fun MapSearchResults(
    searchResultsState: MapSearchResultsState,
    query: String,
    searchResults: List<User>,
    onSearchItemClick: (User) -> Unit
) {
    when (searchResultsState) {
        MapSearchResultsState.NO_QUERY -> EmptyQuerySearchBarState()
        MapSearchResultsState.NO_RESULTS -> NoResultsSearchBarState(query)
        MapSearchResultsState.RESULTS -> SearchResultsList(
            searchResults,
            onSearchItemClick = onSearchItemClick
        )
    }
}

@Composable
private fun SearchResultsList(
    searchResults: List<User>,
    modifier: Modifier = Modifier,
    onSearchItemClick: (User) -> Unit,
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(searchResults) { _, user ->
            val onClick = remember(user) { { onSearchItemClick(user) } }
            SearchItem(text = user.username, onClick)
            Divider()
        }
    }
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

@Composable
private fun EmptyQuerySearchBarState() {
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

@Composable
private fun NoResultsSearchBarState(query: String) {
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
            stringResource(id = R.string.map_search_no_results, query),
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview(widthDp = 300, heightDp = 200)
@Composable
fun NoResultsPreview() {
    NoResultsSearchBarState(query = "not found")
}

@Preview(widthDp = 300, heightDp = 200)
@Composable
fun EmptyQueryPreview() {
    EmptyQuerySearchBarState()
}

@Preview(widthDp = 300, heightDp = 200)
@Composable
fun SearchResultPreview() {
    SearchResultsList(searchResults = listOf(
        User("", "name"),
        User("", "name"),
        User("", "name"),
        User("", "name"),
        User("", "name"),
        User("", "name"),
        User("", "name"),
        User("", "name"),
        User("", "name"),
        User("", "name"),
        User("", "name"),
        User("", "name"),
        User("", "name"),
        User("", "name"),
        User("", "name"),
        User("", "name"),
    ), onSearchItemClick = {})
}