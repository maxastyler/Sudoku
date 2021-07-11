package com.maxtyler.sudoku.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxtyler.sudoku.database.PuzzleSave
import com.maxtyler.sudoku.model.Sudoku
import com.maxtyler.sudoku.repository.PuzzleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class SudokuViewModel @Inject constructor(
    private val puzzleRepository: PuzzleRepository,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {
    private var numberOfClues: Int = 30
    private var puzzleSave: PuzzleSave? = null
    private val _puzzle: MutableStateFlow<Sudoku> = MutableStateFlow(Sudoku())
    val puzzle = _puzzle.asStateFlow()
    private val _controlState: MutableStateFlow<ControlState> = MutableStateFlow(ControlState())
    val controlState = _controlState.asStateFlow()

    val contradictions = _puzzle.mapLatest { it.findContradictions() }

    private var puzzleJob: Job? = null

    init {
        savedStateHandle.get<Int>("numberOfClues")?.let { numberOfClues = it }
        savedStateHandle.get<Long>("gameId")?.let {
            loadPuzzle(it)
        } ?: run { getNewPuzzle(numberOfClues) }
    }

    fun getNewPuzzle(numberFilled: Int) {
        puzzleJob?.cancel()
        puzzleSave = null

        puzzleJob = viewModelScope.launch {
            puzzleRepository.createNewPuzzle(numberFilled)?.collectLatest {
                puzzleSave = it
                _puzzle.emit(
                    Sudoku(
                        clues = it.clues,
                        entries = it.entries,
                        guesses = it.guesses
                    )
                )
            }
        }
    }

    fun loadPuzzle(puzzleId: Long) {
        puzzleJob?.cancel()
        puzzleJob = viewModelScope.launch {
            puzzleRepository.getPuzzle(puzzleId).flowOn(Dispatchers.IO).filterNotNull()
                .collectLatest {
                    puzzleSave = it
                    _puzzle.emit(
                        Sudoku(
                            clues = it.clues,
                            entries = it.entries,
                            guesses = it.guesses
                        )
                    )
                }
        }
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

    fun writeCurrentSave() {
        puzzleSave?.let {
            viewModelScope.launch(Dispatchers.IO) {
                supervisorScope {
                    puzzleRepository.writeSave(
                        it.copy(
                            clues = _puzzle.value.clues,
                            entries = _puzzle.value.entries.filterValues { it != null }
                                .mapValues { (_, v) -> v!! },
                            guesses = _puzzle.value.guesses
                        )
                    )
                }
            }
        }
    }
}