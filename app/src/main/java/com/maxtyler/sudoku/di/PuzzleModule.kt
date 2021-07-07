package com.maxtyler.sudoku.di

import com.maxtyler.sudoku.repository.PuzzleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class PuzzleModule {
    @Provides
    @ViewModelScoped
    fun providePuzzleRepository(): PuzzleRepository = PuzzleRepository()
}