package com.example.smallworld.di

import android.content.Context
import com.example.smallworld.BuildConfig
import com.example.smallworld.R
import com.example.smallworld.data.SmallWorldApi
import com.example.smallworld.data.SmallWorldAuthApi
import com.example.smallworld.data.auth.AuthOkhttpInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DispatcherIO

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrl

@InstallIn(SingletonComponent::class)
@Module
class AppModule {
    @Singleton
    @Provides
    fun provideOkhttp(): OkHttpClient = OkHttpClient.Builder().build()

    @Singleton
    @Provides
    @BaseUrl
    fun provideApiBaseUrl(@ApplicationContext context: Context): String = when (BuildConfig.DEBUG) {
        true -> context.getString(R.string.api_url)
        false -> BuildConfig.API_BASE_URL
    }

    @Singleton
    @Provides
    fun provideAuthApi(okhttp: OkHttpClient, @BaseUrl baseUrl: String): SmallWorldAuthApi =
        Retrofit.Builder()
            .baseUrl("$baseUrl/auth/")
            .client(okhttp)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(SmallWorldAuthApi::class.java)

    @Singleton
    @Provides
    fun provideApi(
        authInterceptor: AuthOkhttpInterceptor,
        @BaseUrl baseUrl: String
    ): SmallWorldApi =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(OkHttpClient.Builder().addInterceptor(authInterceptor).build())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(SmallWorldApi::class.java)

    @DispatcherIO
    @Provides
    fun provideDispatcherIo(): CoroutineDispatcher = Dispatchers.IO

    @ApplicationScope
    @Provides
    fun provideApplicationCoroutineScope(): CoroutineScope =
        CoroutineScope(Dispatchers.Main + SupervisorJob())
}

