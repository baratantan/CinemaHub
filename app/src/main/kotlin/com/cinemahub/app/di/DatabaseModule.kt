package com.cinemahub.app.di

import android.content.Context
import androidx.room.Room
import com.cinemahub.app.data.local.database.CinemaHubDatabase
import com.cinemahub.app.data.local.dao.MovieDao
import com.cinemahub.app.data.local.dao.SeriesDao
import com.cinemahub.app.data.local.dao.WatchHistoryDao
import com.cinemahub.app.data.local.dao.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideCinemaHubDatabase(
        @ApplicationContext context: Context
    ): CinemaHubDatabase {
        return CinemaHubDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideMovieDao(database: CinemaHubDatabase): MovieDao {
        return database.movieDao()
    }

    @Provides
    @Singleton
    fun provideSeriesDao(database: CinemaHubDatabase): SeriesDao {
        return database.seriesDao()
    }

    @Provides
    @Singleton
    fun provideWatchHistoryDao(database: CinemaHubDatabase): WatchHistoryDao {
        return database.watchHistoryDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteDao(database: CinemaHubDatabase): FavoriteDao {
        return database.favoriteDao()
    }
}
