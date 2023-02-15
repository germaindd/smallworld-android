package com.example.smallworld.services

import android.content.Context
import com.example.smallworld.data.auth.models.JwtDto
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(@ApplicationContext context: Context) {
    private val sharedPrefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    private enum class Keys {
        ACCESS_TOKEN,
        REFRESH_TOKEN
    }

    fun setAccessTokens(tokens: JwtDto) {
        sharedPrefs
            .edit()
            .putString(Keys.ACCESS_TOKEN.name, tokens.accessToken)
            .apply()
        sharedPrefs
            .edit()
            .putString(Keys.REFRESH_TOKEN.name, tokens.refreshToken)
            .apply()
    }

    fun isLoggedIn(): Boolean = sharedPrefs.getString(Keys.ACCESS_TOKEN.name, null) != null

    fun requireAccessToken(): String = sharedPrefs.getString(Keys.ACCESS_TOKEN.name, null)!!

    fun requireRefreshToken(): String = sharedPrefs.getString(Keys.REFRESH_TOKEN.name, null)!!
}