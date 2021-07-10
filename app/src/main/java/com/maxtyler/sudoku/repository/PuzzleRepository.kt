package com.maxtyler.sudoku.repository

import android.util.Log
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
import javax.inject.Inject

class PuzzleRepository @Inject constructor(private val puzzleDao: PuzzleDao) {
    private val numberToGenerate: Int = 4
    private val minSolutions: Int = 28

    private val scope = CoroutineScope(Dispatchers.IO)

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

    suspend fun getPuzzle(clueNumber: Int): Flow<Sudoku> {
        Log.d("GAMES", "Getting puzzle...")
        val puzzle = puzzleDao.getFirstGeneratedPuzzle().filterNotNull().first()
        Log.d("GAMES", "Got puzzle...")
        return puzzleDao.transformGeneratedPuzzleToSave(puzzle, clueNumber = clueNumber)!!
            .filterNotNull().mapLatest { Sudoku(it.clues, it.entries, it.guesses) }
    }

    fun generatePuzzle(numberFilled: Int): List<Triple<Int, Int, String>> {
        var puzzle: List<Triple<Int, Int, String>>? = null
        while (puzzle == null) {
            Solver(Sudoku()).findMinSolutions(81 - numberFilled)?.let { puzzle = it }
        }
        return puzzle!!
    }
}