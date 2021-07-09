package com.maxtyler.sudoku.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxtyler.sudoku.model.Sudoku
import com.maxtyler.sudoku.repository.PuzzleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SudokuViewModel @Inject constructor(private val puzzleRepository: PuzzleRepository) :
    ViewModel() {
    private val _puzzle: MutableStateFlow<Sudoku> = MutableStateFlow(Sudoku())
    val puzzle = _puzzle.asStateFlow()
    private val _controlState: MutableStateFlow<ControlState> = MutableStateFlow(ControlState())
    val controlState = _controlState.asStateFlow()

    val contradictions = _puzzle.mapLatest { it.findContradictions() }

    private var puzzleJob: Job? = null

    init {
        getNewPuzzle(30)
    }

    fun getNewPuzzle(numberFilled: Int) {
        puzzleJob?.cancel()
        puzzleJob = viewModelScope.launch(Dispatchers.IO) {
            puzzleRepository.getPuzzle().filterNotNull().first()}
    }

    fun setNewPuzzle(puzzle: Sudoku) {
        _controlState.value = ControlState()
        _puzzle.value = puzzle
    }

    fun toggleEntry(coord: Pair<Int, Int>, entry: Int) =
        _puzzle.value.toggleEntry(coord, entry)?.let { _puzzle.value = it }

    fun toggleGuess(coord: Pair<Int, Int>, guess: Int) =
        _puzzle.value.toggleGuess(coord, guess)?.let {
            _puzzle.value = it
        }

    fun toggleSquare(square: Pair<Int, Int>) {
        when {
            (square.first < 0) or (square.first > 8) or (square.second < 0) or (square.second > 8) -> Unit
            square in _puzzle.value.clues.keys -> Unit
            _controlState.value.selected == square -> _controlState.value =
                _controlState.value.copy(selected = null)
            else -> _controlState.value = _controlState.value.copy(selected = square)
        }
    }
}