package com.maxtyler.sudoku.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxtyler.sudoku.model.Sudoku
import com.maxtyler.sudoku.repository.PuzzleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SudokuViewModel @Inject constructor(private val puzzleRepository: PuzzleRepository) :
    ViewModel() {
    private val _puzzle: MutableStateFlow<Sudoku> = MutableStateFlow(Sudoku())
    private val _controlState: MutableStateFlow<ControlState> = MutableStateFlow(ControlState())
    val controlState = _controlState.asStateFlow()
    val puzzleView: Flow<SudokuDrawState> = _puzzle.mapLatest { SudokuDrawState(it) }

    private var puzzleJob: Job? = null

    init {
        generatePuzzle(31)
    }

    fun generatePuzzle(numberFilled: Int) {
        puzzleJob?.cancel()
        puzzleJob = viewModelScope.launch {
            val sudoku = withContext(Dispatchers.Default) {
                puzzleRepository.getPuzzle(numberFilled = numberFilled)
            }
            setNewPuzzle(sudoku)
        }
    }

    fun setNewPuzzle(puzzle: Sudoku) {
        _controlState.value = ControlState()
        _puzzle.value = puzzle
    }

    fun toggleSquare(square: Pair<Int, Int>) {
        Log.d("GAMES", "${square}")
        when {
            (square.first < 0) or (square.first > 8) or (square.second < 0) or (square.second > 8) -> Unit
            square in _puzzle.value.clues.keys -> Unit
            _controlState.value.selected == square -> _controlState.value =
                _controlState.value.copy(selected = null)
            else -> _controlState.value = _controlState.value.copy(selected = square)
        }
    }
}