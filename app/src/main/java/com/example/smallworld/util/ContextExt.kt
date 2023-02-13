package com.example.smallworld.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/*
* Searches up the context tree for an Activity. Throws an IllegalStateException if it can't find one.
* See https://betterprogramming.pub/how-to-get-activity-from-jetpack-compose-d0af406d534f
*/
inline fun <reified T : Activity> Context.requireActivity(): T {
    var context = this
    while (context is ContextWrapper) {
        if (context is T) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}