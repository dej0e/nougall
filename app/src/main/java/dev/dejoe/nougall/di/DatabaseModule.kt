package dev.dejoe.nougall.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.dejoe.nougall.data.room.AppDatabase
import dev.dejoe.nougall.data.room.MovieDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "movies_db"
        )
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    fun provideMovieDao(db: AppDatabase): MovieDao =
        db.movieDao()
}
