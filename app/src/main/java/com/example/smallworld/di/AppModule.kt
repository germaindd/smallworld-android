package com.example.smallworld.di

import com.example.smallworld.BuildConfig
import com.example.smallworld.data.SmallWorldApi
import com.example.smallworld.data.auth.AuthOkhttpInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

@InstallIn(SingletonComponent::class)
@Module
class AppModule {
    @Singleton
    @Provides
    fun provideOkhttp(): OkHttpClient = OkHttpClient.Builder().build()

    @Singleton
    @Provides
    fun provideRetrofit(authInterceptor: AuthOkhttpInterceptor): SmallWorldApi =
        Retrofit.Builder()
            .client(
                OkHttpClient.Builder().addInterceptor(authInterceptor).build()
            )
            .baseUrl(BuildConfig.API_BASE_URL)
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

