package com.cinemahub.app.di

import com.cinemahub.app.BuildConfig
import com.cinemahub.app.data.network.client.KtorHttpClient
import com.cinemahub.app.data.network.scraper.VideoScraper
import com.cinemahub.app.data.network.tmdb.TmdbApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideKtorHttpClient(): KtorHttpClient = KtorHttpClient()

    @Singleton
    @Provides
    fun provideVideoScraper(): VideoScraper = VideoScraper()

    @Singleton
    @Provides
    fun provideTmdbApiService(): TmdbApiService = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TmdbApiService::class.java)
}
