package com.cinemahub.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cinemahub.app.domain.model.WatchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchHistoryDao {
    @Query("SELECT * FROM watch_history ORDER BY lastWatchedAt DESC")
    fun getWatchHistory(): Flow<List<WatchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addHistory(item: WatchHistory)

    @Query("DELETE FROM watch_history")
    suspend fun clearHistory()

    @Query("SELECT * FROM watch_history WHERE itemId = :itemId AND itemType = :itemType LIMIT 1")
    fun getHistoryByItem(itemId: Int, itemType: String): Flow<WatchHistory?>

    @Query("SELECT * FROM watch_history WHERE itemId = :itemId AND itemType = :itemType LIMIT 1")
    suspend fun getHistoryByItemOnce(itemId: Int, itemType: String): WatchHistory?
}
