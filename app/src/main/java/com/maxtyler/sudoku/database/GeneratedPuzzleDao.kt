package com.maxtyler.sudoku.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GeneratedPuzzleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGeneratedPuzzle(puzzle: GeneratedPuzzle)

    @Query("SELECT * from GeneratedPuzzle where id = :id LIMIT 1")
    fun getGeneratedPuzzle(id: Int): Flow<GeneratedPuzzle?>

    @Query("SELECT * from GeneratedPuzzle")
    fun getGeneratedPuzzles(): Flow<List<GeneratedPuzzle>>
}