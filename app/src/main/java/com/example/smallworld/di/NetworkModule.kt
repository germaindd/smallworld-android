package com.example.smallworld.di

import com.example.smallworld.BuildConfig
import com.example.smallworld.data.SmallWorldApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    // TODO: should this be a Singleton?
    @Singleton
    @Provides
    fun provideRetrofit(): SmallWorldApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(SmallWorldApi::class.java)
}