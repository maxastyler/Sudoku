package com.maxtyler.sudoku.repository

import com.maxtyler.sudoku.database.GeneratedPuzzle
import com.maxtyler.sudoku.database.PuzzleDao
import com.maxtyler.sudoku.database.PuzzleSave
import com.maxtyler.sudoku.model.Difficulty
import com.maxtyler.sudoku.model.Solver
import com.maxtyler.sudoku.model.Sudoku
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PuzzleRepository @Inject constructor(private val puzzleDao: PuzzleDao) {
    private val numberToGenerate: Int = 8
    private val numberToShuffle: Int = 4

    private val scope = CoroutineScope(Dispatchers.IO)

    val saves: Flow<List<PuzzleSave>>
        get() = puzzleDao.getPuzzleSavesByCompletion(0)

    init {
        Difficulty.values().forEach {
            launchGenerationForDifficulty(it)
        }
    }

    fun launchGenerationForDifficulty(difficulty: Difficulty) = scope.launch {
        puzzleDao.generatedPuzzleCount(difficulty.clues).flowOn(Dispatchers.IO).collectLatest {
            if (it < numberToGenerate) {
                val puzzle = withContext(Dispatchers.Default) {
                    generatePuzzle(difficulty.clues)
                }
                val p = GeneratedPuzzle(clues = puzzle, minimumClues = difficulty.clues)
                puzzleDao.insertGeneratedPuzzle(*(Array(numberToShuffle) { p.shuffle() } + p))
            }
        }
    }

    fun generatedPuzzleCount(difficulty: Difficulty): Flow<Int> =
        puzzleDao.generatedPuzzleCount(difficulty.clues)

    private fun puzzleSaveToSudoku(puzzleSave: PuzzleSave): Sudoku =
        Sudoku(
            clues = puzzleSave.clues.toPersistentMap(),
            entries = puzzleSave.entries.toPersistentMap(),
            guesses = puzzleSave.guesses.toPersistentMap()
        )

    suspend fun createNewPuzzle(difficulty: Difficulty): Flow<PuzzleSave>? {
        val puzzle = puzzleDao.getFirstGeneratedPuzzleFlow(difficulty.clues).filterNotNull().first()
        return puzzleDao.transformGeneratedPuzzleToSave(puzzle, clueNumber = difficulty.clues)
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