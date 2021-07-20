package com.maxtyler.sudoku.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.Duration
import java.time.Instant
import java.util.*

@Dao
interface PuzzleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneratedPuzzle(puzzle: GeneratedPuzzle)

    @Query("SELECT * from GeneratedPuzzle where id = :id LIMIT 1")
    fun getGeneratedPuzzle(id: Long): Flow<GeneratedPuzzle?>

    @Query("SELECT * from GeneratedPuzzle ORDER BY id ASC LIMIT 1")
    fun getFirstGeneratedPuzzleFlow(): Flow<GeneratedPuzzle?>

    @Query("SELECT * from GeneratedPuzzle")
    fun getGeneratedPuzzles(): Flow<List<GeneratedPuzzle>>

    @Delete
    suspend fun deletePuzzle(vararg puzzles: GeneratedPuzzle): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPuzzleSave(puzzleSave: PuzzleSave): Long

    @Query("SELECT * from puzzlesave where completed=0 ORDER BY dateWritten DESC")
    fun getPuzzleSaves(): Flow<List<PuzzleSave>>

    @Query("SELECT * from puzzlesave where id=:id LIMIT 1")
    fun getPuzzleSave(id: Long): Flow<PuzzleSave?>

    @Delete
    suspend fun deletePuzzleSave(vararg puzzle: PuzzleSave): Int

    @Query("SELECT COUNT(id) FROM generatedpuzzle")
    fun generatedPuzzleCount(): Flow<Int>

    /**
     * Turn a generated puzzle into an empty puzzle save with the correct number of clues, failing
     * if the number of clues is less than the minimum allowed for the puzzle
     * @param puzzle The generated puzzle
     * @param clueNumber The number of clues to add to the puzzle
     * @return True if the transaction succeeded, false if not
     */
    @Transaction
    suspend fun transformGeneratedPuzzleToSave(
        puzzle: GeneratedPuzzle,
        clueNumber: Int
    ): Flow<PuzzleSave?>? {
        return when {
            clueNumber < puzzle.minimumClues -> null
            deletePuzzle(puzzle) == 0 -> null
            else -> {
                val save =
                    PuzzleSave(
                        clues = puzzle.clues.take(clueNumber)
                            .map { (row, col, v) -> (row to col) to v.toInt() }
                            .toMap(),
                        entries = mapOf(),
                        guesses = mapOf(),
                        dateWritten = Date.from(Instant.now()),
                        puzzleTime = Duration.ZERO)
                val puzzleId = insertPuzzleSave(save)
                getPuzzleSave(puzzleId)
            }
        }
    }
}