package com.example.smallworld.ui.snackbar

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackBarMessageBus @Inject constructor() {
    private val _messagesFlow = MutableSharedFlow<SnackBarMessage>()
    val messagesFlow: Flow<SnackBarMessage> = _messagesFlow.conflate()

    suspend fun sendMessage(snackBarMessage: SnackBarMessage) = withContext(Dispatchers.Main) {
        _messagesFlow.emit(snackBarMessage)
    }
}