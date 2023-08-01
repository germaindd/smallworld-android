package com.example.smallworld.data.auth

import android.content.Context
import com.example.smallworld.data.auth.models.JwtDto
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthTokenStore @Inject constructor(@ApplicationContext context: Context) {
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

    fun getAccessToken(): String? = sharedPrefs.getString(Keys.ACCESS_TOKEN.name, null)

    fun getRefreshToken(): String? = sharedPrefs.getString(Keys.REFRESH_TOKEN.name, null)

    fun requireAccessToken(): String = getAccessToken() ?: error("No access token in storage.")

    fun requireRefreshToken(): String = getRefreshToken() ?: error("No refresh token in storage.")
}