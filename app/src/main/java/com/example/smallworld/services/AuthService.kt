package com.example.smallworld.services

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(@ApplicationContext context: Context) {
    private val sharedPrefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    private enum class Keys {
        ACCESS_TOKEN
    }

    fun setAccessToken(accessToken: String) {
        sharedPrefs
            .edit()
            .putString(Keys.ACCESS_TOKEN.name, accessToken)
            .apply()
    }

    fun isLoggedIn(): Boolean = sharedPrefs.getString(Keys.ACCESS_TOKEN.name, null) != null

    fun getAccessTokenThrowable(): String = sharedPrefs.getString(Keys.ACCESS_TOKEN.name, null)!!
}