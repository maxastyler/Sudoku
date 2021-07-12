package com.maxtyler.sudoku.ui

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

    init {
        viewModelScope.launch {
            puzzleRepository.generatedPuzzleCount.collectLatest {
                Log.d(
                    "GAMES",
                    "The number of generated saves: ${it}"
                )
            }
        }
    }

    fun deletePuzzleSave(puzzleSave: PuzzleSave) {
        viewModelScope.launch(Dispatchers.IO) { puzzleRepository.deleteSave(puzzleSave) }
    }

    suspend fun createPuzzle(difficulty: Difficulty): Long? {
        return withContext(Dispatchers.IO) {
            puzzleRepository.createNewPuzzle(difficulty.clues)?.first()?.id
        }
    }

    enum class Difficulty(val clues: Int) {
        Easy(50), Medium(40), Hard(30)
    }
}