package com.example.smallworld.data.auth

import com.example.smallworld.data.SmallWorldAuthApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.HttpException
import timber.log.Timber
import java.net.HttpURLConnection
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class AuthOkhttpInterceptor @Inject constructor(
    private val authTokenStore: AuthTokenStore,
    private val authApi: SmallWorldAuthApi
) : Interceptor {
    private val isRefreshingToken = AtomicBoolean()
    private fun authenticateRequest(request: Request) = request
        .newBuilder()
        .addHeader("authorization", "Bearer ${authTokenStore.requireAccessToken()}")
        .build()

    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        while (isRefreshingToken.get()) delay(25)

        val uuid = UUID.randomUUID()
        Timber.d("ID: $uuid \nRequesting ${chain.request().url}")
        val response = chain.proceed(authenticateRequest(chain.request()))
        if (response.isSuccessful || response.code != HttpURLConnection.HTTP_UNAUTHORIZED) {
            Timber.d("ID: $uuid \nSuccessful response from ${chain.request().url}")
            return@runBlocking response
        }
        response.close()
        Timber.d("ID: $uuid \nUnauthorized response from ${chain.request().url}")

        // atomically reads and updates the value of isRefreshingToken to true if it is indeed false,
        // and returns whether or not it was false.
        // see https://www.baeldung.com/java-volatile-variables-thread-safety explanation on atomic variables
        val notAlreadyRefreshing = isRefreshingToken.compareAndSet(false, true)


        when {
            notAlreadyRefreshing -> {
                Timber.d("ID: $uuid \nRefreshing tokens")
                // request new tokens from server
                val tokens = try {
                    authApi.refreshTokens("Bearer ${authTokenStore.requireRefreshToken()}")
                } catch (e: HttpException) {
                    if (e.code() == HttpURLConnection.HTTP_UNAUTHORIZED)
                        TODO("log the user out")
                    else throw e // let the caller handle any unexpected exceptions
                }

                // update access tokens
                authTokenStore.setAccessTokens(tokens)
                Timber.d("ID: $uuid \nToken refresh successful")
                isRefreshingToken.set(false)
            }
            else -> {
                Timber.d("ID: $uuid \nToken refresh in progress. Waiting...")
                while (isRefreshingToken.get()) delay(25)
            }
        }
        Timber.d("ID: $uuid \nReattempting...")
        chain.proceed(authenticateRequest(chain.request()))
    }
}