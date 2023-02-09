package com.example.smallworld.ui.snackbar

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SnackBarViewModel @Inject constructor(
    private val messageBus: SnackBarMessageBus
) : ViewModel() {
    val messagesFlow get() = messageBus.messagesFlow
}