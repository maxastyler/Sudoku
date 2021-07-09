package com.maxtyler.sudoku.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = arrayOf(PuzzleSave::class, GeneratedPuzzle::class), version = 1)
@TypeConverters(SudokuConverters::class)
abstract class SudokuDatabase : RoomDatabase() {
    abstract fun getPuzzleSaveDao(): PuzzleSaveDao
    abstract fun getGeneratedPuzzleDao(): GeneratedPuzzleDao
}