package com.maxtyler.sudoku.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxtyler.sudoku.database.PuzzleSave
import com.maxtyler.sudoku.repository.PuzzleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(private val puzzleRepository: PuzzleRepository) :
    ViewModel() {

    val saves = puzzleRepository.saves
    val puzzleCount = puzzleRepository.generatedPuzzleCount

    /**
     * Delete the given puzzle save from the database
     * @param puzzleSave The puzzle to delete
     */
    fun deletePuzzleSave(puzzleSave: PuzzleSave) {
        viewModelScope.launch(Dispatchers.IO) { puzzleRepository.deleteSave(puzzleSave) }
    }

    /**
     * Create a puzzle with the given difficulty
     * @param difficulty The difficulty of the puzzle
     * @return The id of the puzzle or null if it couldn't be created
     */
    suspend fun createPuzzle(difficulty: Difficulty): Long? {
        return withContext(Dispatchers.IO) {
            puzzleRepository.createNewPuzzle(difficulty.clues)?.first()?.id
        }
    }

    /**
     * The different difficulty settings
     * @param clues The number of clues this difficulty setting has in the puzzle
     */
    enum class Difficulty(val clues: Int) {
        Easy(50), Medium(40), Hard(30)
    }
}