package com.example.smallworld.data.auth

import com.example.smallworld.BuildConfig
import com.example.smallworld.data.auth.models.JwtDto
import com.example.smallworld.services.AuthService
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.EMPTY_REQUEST
import java.net.HttpURLConnection
import javax.inject.Inject

class AuthOkhttpInterceptor @Inject constructor(
    private val authService: AuthService,
    private val okhttp: OkHttpClient
) : Interceptor {
    private val jwtDtoAdapter = Moshi.Builder().build().adapter(JwtDto::class.java)

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.url.toString().startsWith(BuildConfig.API_BASE_URL)
            && request.url.pathSegments[0] != "auth"
        ) {
            val accessToken = authService.requireAccessToken()
            val authenticatedRequest = request
                .newBuilder()
                .addHeader("authorization", "Bearer $accessToken")
                .build()
            val response = chain.proceed(authenticatedRequest)
            if (response.isSuccessful
                || response.code != HttpURLConnection.HTTP_UNAUTHORIZED
            ) return response
            response.close()

            val refreshTokenRequest =
                Request.Builder()
                    .url("${BuildConfig.API_BASE_URL}auth/refresh-tokens")
                    .post(EMPTY_REQUEST)
                    .addHeader("authorization", "Bearer ${authService.requireRefreshToken()}")
                    .build()
            val newTokensResponse = okhttp.newCall(refreshTokenRequest).execute()

            if (newTokensResponse.isSuccessful) {
                val responseBody = newTokensResponse.body
                    ?: error("Missing respones body in 200 Success /auth/refresh-tokens response")
                val tokens =
                    jwtDtoAdapter
                        .fromJson(responseBody.string())
                        ?: error("Invalid json in /auth/refresh-tokens response")
                authService.setAccessTokens(tokens)
                val newRequest = request
                    .newBuilder()
                    .addHeader("authorization", "Bearer ${tokens.accessToken}")
                    .build()

                return chain.proceed(newRequest)
            } else {
                if (newTokensResponse.code == HttpURLConnection.HTTP_UNAUTHORIZED) TODO("log the user out")
                TODO("Throw some sort of exception")
            }
        }
        return chain.proceed(request)
    }
}