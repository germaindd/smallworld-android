package com.example.smallworld.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smallworld.data.search.SearchRepository
import com.example.smallworld.data.search.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MapScreenState(
    val query: String = "",
    val searchResults: List<User> = emptyList()
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {
    private val query: MutableStateFlow<String> = MutableStateFlow("")

    @OptIn(FlowPreview::class)
    private val searchResults: Flow<List<User>> = query
        .debounce(200)
        .map { if (it.isBlank()) emptyList() else searchRepository.search(it) }

    private val _state = MutableStateFlow(MapScreenState())
    val state: StateFlow<MapScreenState> = _state

    init {
        viewModelScope.launch {
            combine(query, searchResults) { query, searchResults ->
                MapScreenState(query, searchResults)
            }.collect { _state.value = it }
        }

    }

    fun onQueryChange(value: String) {
        query.value = value
    }
}