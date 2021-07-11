package com.maxtyler.sudoku.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    suspend fun createPuzzle(): Long? {
        Log.d("GAMES", "Creating! :)")
        return withContext(Dispatchers.IO) {
            puzzleRepository.createNewPuzzle(30)?.first()?.id
        }
    }
}