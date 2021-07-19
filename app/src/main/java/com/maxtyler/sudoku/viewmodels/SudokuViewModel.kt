package com.maxtyler.sudoku.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxtyler.sudoku.database.PuzzleSave
import com.maxtyler.sudoku.model.Sudoku
import com.maxtyler.sudoku.repository.PuzzleRepository
import com.maxtyler.sudoku.ui.ControlState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class SudokuViewModel @Inject constructor(
    private val puzzleRepository: PuzzleRepository,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {
    private var numberOfClues: Int = 30
    private var puzzleSave: PuzzleSave? = null
    private val _puzzles: MutableStateFlow<List<Sudoku>> = MutableStateFlow(listOf())
    val puzzles = _puzzles.asStateFlow()
    private val _redoQueue: MutableStateFlow<List<Sudoku>> = MutableStateFlow(listOf())
    val redoQueue = _redoQueue.asStateFlow()

    //    private val _puzzle: MutableStateFlow<Sudoku> = MutableStateFlow(Sudoku())
    val puzzle = _puzzles.map { it.lastOrNull() }
    private val _controlState: MutableStateFlow<ControlState> = MutableStateFlow(ControlState())
    val controlState = _controlState.asStateFlow()
    val completed: Flow<Boolean> = puzzle.map { it?.completed() ?: false }

    val contradictions = puzzle.mapLatest { it?.findContradictions() ?: listOf() }

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
                setPuzzle(
                    Sudoku(
                        clues = it.clues.toPersistentMap(),
                        entries = it.entries.toPersistentMap(),
                        guesses = it.guesses.toPersistentMap(),
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
                    setPuzzle(
                        Sudoku(
                            clues = it.clues.toPersistentMap(),
                            entries = it.entries.toPersistentMap(),
                            guesses = it.guesses.toPersistentMap(),
                        )
                    )
                }
        }
    }

    fun setNewPuzzle(puzzle: Sudoku) {
        _controlState.value = ControlState()
        setPuzzle(puzzle)
    }

    fun toggleEntry(coord: Pair<Int, Int>, entry: Int) {
        _puzzles.value.lastOrNull()?.toggleEntry(coord, entry)?.let {
            setPuzzle(it)
        }
    }

    fun toggleGuess(coord: Pair<Int, Int>, guess: Int) =
        _puzzles.value.lastOrNull()?.toggleGuess(coord, guess)?.let {
            setPuzzle(it)
        }

    fun clearAllEntries() {
        _puzzles.value.lastOrNull()?.let {
            setPuzzle(it.clearEntries())
        }
    }

    fun cleanGuesses() {
        _puzzles.value.lastOrNull()?.let {
            setPuzzle(it.cleanAllGuesses())
        }
    }

    fun clearAllGuesses() {
        _puzzles.value.lastOrNull()?.let {
            setPuzzle(it.clearGuesses())
        }
    }

    fun clearAllValues() {
        _puzzles.value.lastOrNull()?.let {
            setPuzzle(it.clearAll())
        }
    }

    fun setPuzzle(sudoku: Sudoku) {
        if (sudoku != _puzzles.value.lastOrNull()) {
            _puzzles.value = _puzzles.value + sudoku
            _redoQueue.value = listOf()
        }
    }

    fun toggleSquare(square: Pair<Int, Int>) {
        when {
            (square.first < 0) or (square.first > 8) or (square.second < 0) or (square.second > 8) -> Unit
            _puzzles.value.lastOrNull()?.clues?.keys?.let { square in it } ?: false -> Unit
            _controlState.value.selected == square -> _controlState.value =
                _controlState.value.copy(selected = null)
            else -> _controlState.value = _controlState.value.copy(selected = square)
        }
    }

    fun writeCurrentSave() = puzzleSave?.let { ps ->
        _puzzles.value.lastOrNull()?.let {
            puzzleRepository.writeSave(
                ps.copy(
                    clues = it.clues,
                    entries = it.entries.filterValues { it != null }
                        .mapValues { (_, v) -> v!! }.toPersistentMap(),
                    guesses = it.guesses,
                    completed = runBlocking { completed.first() }
                )
            )
        }
    }

    fun undo() {
        if (_puzzles.value.count() > 1) {
            _puzzles.value.lastOrNull()?.let { last ->
                _redoQueue.value += last
                _puzzles.value = _puzzles.value.dropLast(1)
            }
        }
    }

    fun redo() {
        _redoQueue.value.lastOrNull()?.let { last ->
            _puzzles.value += last
            _redoQueue.value = _redoQueue.value.dropLast(1)
        }
    }
}