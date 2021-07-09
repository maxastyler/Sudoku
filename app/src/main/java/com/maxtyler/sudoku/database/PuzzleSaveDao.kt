package com.maxtyler.sudoku.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PuzzleSaveDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPuzzle(puzzleSave: PuzzleSave)

    @Query("SELECT * from puzzlesave")
    fun getPuzzles(): Flow<List<PuzzleSave>>

    @Query("SELECT * from puzzlesave where id=:id LIMIT 1")
    fun getPuzzle(id: Int): Flow<PuzzleSave?>

    @Delete
    fun deletePuzzleSave(vararg puzzle: PuzzleSave): Int
}