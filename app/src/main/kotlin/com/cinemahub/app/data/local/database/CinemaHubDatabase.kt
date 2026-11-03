package com.cinemahub.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cinemahub.app.data.local.converters.Converters
import com.cinemahub.app.data.local.dao.FavoriteDao
import com.cinemahub.app.data.local.dao.MovieDao
import com.cinemahub.app.data.local.dao.SeriesDao
import com.cinemahub.app.data.local.dao.WatchHistoryDao
import com.cinemahub.app.domain.model.Favorite
import com.cinemahub.app.domain.model.Movie
import com.cinemahub.app.domain.model.Series
import com.cinemahub.app.domain.model.WatchHistory

@Database(
    entities = [Movie::class, Series::class, Favorite::class, WatchHistory::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CinemaHubDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun seriesDao(): SeriesDao
    abstract fun watchHistoryDao(): WatchHistoryDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE watch_history ADD COLUMN videoUrl TEXT NOT NULL DEFAULT ''")
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS index_watch_history_itemId_itemType " +
                        "ON watch_history(itemId, itemType)"
                )
            }
        }

        @Volatile
        private var INSTANCE: CinemaHubDatabase? = null

        fun getDatabase(context: Context): CinemaHubDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CinemaHubDatabase::class.java,
                    "cinemahub_database"
                ).addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
