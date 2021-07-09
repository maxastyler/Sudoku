package com.maxtyler.sudoku.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = arrayOf(Puzzle::class), version = 1)
@TypeConverters(SudokuConverters::class)
abstract class SudokuDatabase : RoomDatabase() {
    abstract fun puzzleDao(): PuzzleDao
}