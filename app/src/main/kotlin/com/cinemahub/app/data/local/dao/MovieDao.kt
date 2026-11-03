package com.cinemahub.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cinemahub.app.domain.model.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY id DESC")
    fun getAllMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE id = :movieId")
    fun getMovie(movieId: Int): Flow<Movie?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

    @Update
    suspend fun updateMovie(movie: Movie)

    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchMovies(query: String): Flow<List<Movie>>
}