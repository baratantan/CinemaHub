package com.cinemahub.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cinemahub.app.domain.model.Series
import kotlinx.coroutines.flow.Flow

@Dao
interface SeriesDao {
    @Query("SELECT * FROM series ORDER BY id DESC")
    fun getAllSeries(): Flow<List<Series>>

    @Query("SELECT * FROM series WHERE id = :seriesId")
    fun getSeries(seriesId: Int): Flow<Series?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeries(series: Series)

    @Query("SELECT * FROM series WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchSeries(query: String): Flow<List<Series>>
}