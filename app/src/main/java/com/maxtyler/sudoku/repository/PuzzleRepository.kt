package com.maxtyler.sudoku.repository

import com.maxtyler.sudoku.database.GeneratedPuzzle
import com.maxtyler.sudoku.database.PuzzleDao
import com.maxtyler.sudoku.model.Solver
import com.maxtyler.sudoku.model.Sudoku
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PuzzleRepository @Inject constructor(private val puzzleDao: PuzzleDao) {
    private val numberToGenerate: Int = 4
    private val minSolutions: Int = 28

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
    }

    suspend fun getPuzzle(): SharedFlow<Sudoku?> {
        puzzleDao.getGeneratedPuzzles().collectLatest {
            if (it.size < numberToGenerate) {
                (0 until numberToGenerate).forEach {
                    val puzzle = withContext(Dispatchers.Default) { generatePuzzle(minSolutions) }
                    puzzleDao.insertGeneratedPuzzle(
                        GeneratedPuzzle(clues = puzzle, minimumClues = minSolutions)
                    )
                }
            }
        }
        return flowOf(null).shareIn(scope, started = SharingStarted.Eagerly)
    }

    suspend fun generatePuzzle(numberFilled: Int): List<Triple<Int, Int, String>> {
        var puzzle: List<Triple<Int, Int, String>>? = null
        while (puzzle == null) {
            Solver(Sudoku()).findMinSolutions(81 - numberFilled)?.let { puzzle = it }
        }
        return puzzle!!
    }
}