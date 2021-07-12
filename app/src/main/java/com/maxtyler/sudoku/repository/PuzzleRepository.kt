package com.maxtyler.sudoku.repository

import com.maxtyler.sudoku.database.GeneratedPuzzle
import com.maxtyler.sudoku.database.PuzzleDao
import com.maxtyler.sudoku.database.PuzzleSave
import com.maxtyler.sudoku.model.Solver
import com.maxtyler.sudoku.model.Sudoku
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.*
import javax.inject.Inject

class PuzzleRepository @Inject constructor(private val puzzleDao: PuzzleDao) {
    private val numberToGenerate: Int = 4
    private val minSolutions: Int = 30

    private val scope = CoroutineScope(Dispatchers.IO)

    val saves: Flow<List<PuzzleSave>>
        get() = puzzleDao.getPuzzleSaves()

    val generatedPuzzleCount: Flow<Int>
        get() = puzzleDao.generatedPuzzleCount()

    init {
        scope.launch {
            puzzleDao.generatedPuzzleCount().flowOn(Dispatchers.IO).collectLatest {
                if (it < numberToGenerate) {
                    val puzzle = withContext(Dispatchers.Default) {
                        generatePuzzle(minSolutions)
                    }
                    puzzleDao.insertGeneratedPuzzle(
                        GeneratedPuzzle(
                            clues = puzzle,
                            minimumClues = minSolutions
                        )
                    )
                }
            }
        }
    }

    private fun puzzleSaveToSudoku(puzzleSave: PuzzleSave): Sudoku =
        Sudoku(clues = puzzleSave.clues, entries = puzzleSave.entries, guesses = puzzleSave.guesses)

    suspend fun createNewPuzzle(clueNumber: Int): Flow<PuzzleSave>? {
        val puzzle = puzzleDao.getFirstGeneratedPuzzleFlow().filterNotNull().first()
        return puzzleDao.transformGeneratedPuzzleToSave(puzzle, clueNumber = clueNumber)
            ?.filterNotNull()
    }

    suspend fun deleteSave(puzzleSave: PuzzleSave) = puzzleDao.deletePuzzleSave(puzzleSave)

    fun getPuzzle(puzzleId: Long): Flow<PuzzleSave?> = puzzleDao.getPuzzleSave(puzzleId)

    private fun generatePuzzle(numberFilled: Int): List<Triple<Int, Int, String>> {
        var puzzle: List<Triple<Int, Int, String>>? = null
        while (puzzle == null) {
            Solver(Sudoku()).findMinSolutions(81 - numberFilled)?.let { puzzle = it }
        }
        return puzzle!!
    }

    fun writeSave(puzzleSave: PuzzleSave) {
        scope.launch(Dispatchers.IO) {
            puzzleDao.insertPuzzleSave(puzzleSave.copy(dateWritten = Date.from(Instant.now())))
        }
    }
}