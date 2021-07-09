package com.maxtyler.sudoku.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PuzzleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun storePuzzle(puzzle: Puzzle)

    @Query("SELECT * from puzzle")
    fun getPuzzles(): Flow<List<Puzzle>>

    @Query("SELECT * from puzzle where id=:id LIMIT 1")
    fun getPuzzle(id: Int): Flow<Puzzle>
}