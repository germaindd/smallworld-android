package com.example.smallworld.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import com.example.smallworld.BuildConfig
import timber.log.Timber

class Ref(var value: Int)

// Note the inline function below which ensures that this function is essentially
// copied at the call site to ensure that its logging only recompositions from the
// original call site.
@Composable
inline fun LogCompositions(msg: String = "") {
    if (BuildConfig.DEBUG) {
        val ref = remember { Ref(1) }
        SideEffect { ref.value++ }
        Timber.d("Compositions: $msg ${ref.value}")
    }
}