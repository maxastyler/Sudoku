package com.maxtyler.sudoku.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = arrayOf(PuzzleSave::class, GeneratedPuzzle::class), version = 4)
@TypeConverters(SudokuConverters::class)
abstract class SudokuDatabase : RoomDatabase() {
    abstract fun getGeneratedPuzzleDao(): PuzzleDao
}