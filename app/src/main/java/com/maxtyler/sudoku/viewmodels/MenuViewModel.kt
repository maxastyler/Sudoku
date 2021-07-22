package com.maxtyler.sudoku.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxtyler.sudoku.database.PuzzleSave
import com.maxtyler.sudoku.model.Difficulty
import com.maxtyler.sudoku.repository.PuzzleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(private val puzzleRepository: PuzzleRepository) :
    ViewModel() {

    val saves = puzzleRepository.saves

    /**
     * Delete the given puzzle save from the database
     * @param puzzleSave The puzzle to delete
     */
    fun deletePuzzleSave(puzzleSave: PuzzleSave) {
        viewModelScope.launch(Dispatchers.IO) { puzzleRepository.deleteSave(puzzleSave) }
    }

    fun puzzleCount(difficulty: Difficulty) =
        puzzleRepository.generatedPuzzleCount(difficulty = difficulty)

    /**
     * Create a puzzle with the given difficulty
     * @param difficulty The difficulty of the puzzle
     * @return The id of the puzzle or null if it couldn't be created
     */
    suspend fun createPuzzle(difficulty: Difficulty): Long? {
        return withContext(Dispatchers.IO) {
            puzzleRepository.createNewPuzzle(difficulty)?.first()?.id
        }
    }

}