package com.maxtyler.sudoku.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GeneratedPuzzleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGeneratedPuzzle(puzzle: GeneratedPuzzle)

    @Query("SELECT * from GeneratedPuzzle where id = :id LIMIT 1")
    fun getGeneratedPuzzle(id: Int): Flow<GeneratedPuzzle?>

    @Query("SELECT * from GeneratedPuzzle")
    fun getGeneratedPuzzles(): Flow<List<GeneratedPuzzle>>

    @Delete
    fun deletePuzzle(puzzle: GeneratedPuzzle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPuzzle(puzzleSave: PuzzleSave)

    /**
     * Turn a generated puzzle into an empty puzzle save with the correct number of clues, failing
     * if the number of clues is less than the minimum allowed for the puzzle
     * @param puzzle The generated puzzle
     * @param clueNumber The number of clues to add to the puzzle
     * @return True if the transaction succeeded, false if not
     */
    @Transaction
    fun transformGeneratedPuzzleToSave(puzzle: GeneratedPuzzle, clueNumber: Int): Boolean {
        return if (clueNumber >= puzzle.minimumClues) {
            deletePuzzle(puzzle)
            val save =
                PuzzleSave(clues = puzzle.clues.map { (row, col, v) -> (row to col) to v.toInt() }
                    .toMap(), entries = mapOf(), guesses = mapOf())
            insertPuzzle(save)
            true
        } else false
    }
}