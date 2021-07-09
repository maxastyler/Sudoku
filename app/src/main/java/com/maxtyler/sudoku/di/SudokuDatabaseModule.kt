package com.maxtyler.sudoku.di

import android.content.Context
import androidx.room.Room
import com.maxtyler.sudoku.database.PuzzleDao
import com.maxtyler.sudoku.database.SudokuDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SudokuDatabaseModule {
    @Provides
    @Singleton
    fun provideSudokuDatabase(@ApplicationContext context: Context): SudokuDatabase =
        Room.databaseBuilder(context, SudokuDatabase::class.java, "sudoku_database")
            .fallbackToDestructiveMigration().enableMultiInstanceInvalidation().build()

    @Provides
    fun providePuzzleDao(db: SudokuDatabase): PuzzleDao = db.getGeneratedPuzzleDao()
}