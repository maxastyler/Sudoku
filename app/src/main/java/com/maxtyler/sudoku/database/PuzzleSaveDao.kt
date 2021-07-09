package com.maxtyler.sudoku.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PuzzleSaveDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPuzzle(puzzleSave: PuzzleSave)

    @Query("SELECT * from puzzlesave")
    fun getPuzzles(): Flow<List<PuzzleSave>>

    @Query("SELECT * from puzzlesave where id=:id LIMIT 1")
    fun getPuzzle(id: Int): Flow<PuzzleSave?>
}