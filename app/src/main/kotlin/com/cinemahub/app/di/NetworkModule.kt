package com.cinemahub.app.di

import com.cinemahub.app.data.network.client.KtorHttpClient
import com.cinemahub.app.data.network.scraper.VideoScraper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideKtorHttpClient(): KtorHttpClient {
        return KtorHttpClient()
    }

    @Singleton
    @Provides
    fun provideVideoScraper(): VideoScraper {
        return VideoScraper()
    }
}
