package com.cinemahub.app.di

import android.content.Context
import com.cinemahub.app.data.repository.MovieReviewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReviewsModule {
    @Provides
    @Singleton
    fun provideMovieReviewsRepository(
        @ApplicationContext context: Context
    ): MovieReviewsRepository = MovieReviewsRepository(context)
}
