package com.example.smallworld.util

import android.util.Log
import androidx.lifecycle.ViewModel

fun ViewModel.logError(throwable: Throwable) = Log.e(this::class.simpleName, null, throwable)