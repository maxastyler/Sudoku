package com.maxtyler.sudoku.viewmodels

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maxtyler.sudoku.database.PuzzleSave
import com.maxtyler.sudoku.model.Sudoku
import com.maxtyler.sudoku.model.Timer
import com.maxtyler.sudoku.repository.PuzzleRepository
import com.maxtyler.sudoku.ui.utils.ControlState
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
    private val vibrator: Vibrator,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {
    private var vibrationJob: Job? = null
    private var vibrationEffect = VibrationEffect.createOneShot(100L, 200)
    private var numberOfClues: Int = 30
    private var puzzleSave: PuzzleSave? = null
    private val _puzzles: MutableStateFlow<List<Sudoku>> = MutableStateFlow(listOf())
    private val _redoQueue: MutableStateFlow<List<Sudoku>> = MutableStateFlow(listOf())
    private val _controlState: MutableStateFlow<ControlState> = MutableStateFlow(ControlState())
    private val _completed: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val completed = _completed.asStateFlow()

    private val timer = Timer(viewModelScope, completed)

    val puzzles = _puzzles.asStateFlow()
    val redoQueue = _redoQueue.asStateFlow()
    val puzzle = _puzzles.map { it.lastOrNull() }
    val controlState = _controlState.asStateFlow()

    val time = timer.duration

    val contradictions = puzzle.mapLatest { it?.findContradictions() ?: listOf() }

    private var puzzleJob: Job? = null

    init {
        savedStateHandle.get<Int>("numberOfClues")?.let { numberOfClues = it }
        savedStateHandle.get<Long>("gameId")?.let {
            loadPuzzle(it)
        } ?: run { getNewPuzzle(numberOfClues) }

        controlTimerWithCompletionState()
    }

    /**
     * Start a coroutine which collected on completion state and stops/starts the timer based on it
     */
    fun controlTimerWithCompletionState() {
        viewModelScope.launch {
            puzzle.collectLatest { _completed.emit(it?.completed() ?: false) }
            completed.collect {
                when (it) {
                    true -> timer.stopTimer()
                    false -> timer.startTimer()
                }
            }
        }
    }

    /**
     * Incorporate the given puzzle save into this viewmodel
     * @param s The save
     */
    fun setFromPuzzleSave(s: PuzzleSave) {
        puzzleSave = s
        timer.setTime(s.puzzleTime)
        if (setNewPuzzle(
                Sudoku(
                    clues = s.clues.toPersistentMap(),
                    entries = s.entries.toPersistentMap(),
                    guesses = s.guesses.toPersistentMap(),
                )
            )
        ) {
            // If the puzzle was different to the previous one, then start the timer
            timer.startTimer()
        }
    }

    /**
     * Get a new puzzle with the given number filled, starting a job if needed
     * @param numberFilled The number of clues to have in the puzzle
     */
    fun getNewPuzzle(numberFilled: Int) {
        puzzleJob?.cancel()
        puzzleJob = viewModelScope.launch {
            puzzleRepository.createNewPuzzle(numberFilled)?.collectLatest { setFromPuzzleSave(it) }
        }
    }

    /**
     * Load a puzzle from the database with the given id
     * @param puzzleId The database id of the puzzle
     */
    fun loadPuzzle(puzzleId: Long) {
        puzzleJob?.cancel()
        puzzleJob = viewModelScope.launch {
            puzzleRepository.getPuzzle(puzzleId).flowOn(Dispatchers.IO).filterNotNull()
                .collectLatest { setFromPuzzleSave(it) }
        }
    }

    /**
     * Set a new puzzle
     * @param puzzle The new puzzle
     * @return true if puzzle was set, false if not
     */
    fun setNewPuzzle(puzzle: Sudoku): Boolean {
        _controlState.value = ControlState()
        return setPuzzle(puzzle)
    }

    /**
     * Toggle the entry at the given coordinate
     * @param coord The coordinate to toggle at
     * @param entry The number to toggle
     */
    fun toggleEntry(coord: Pair<Int, Int>, entry: Int) {
        _puzzles.value.lastOrNull()?.toggleEntry(coord, entry)?.let {
            vibrate(vibrationEffect)
            setPuzzle(it)
        }
    }

    /**
     * Toggle a guess at the given coordinate
     * @param coord The coordinate to toggle at
     * @param guess The number to enter
     */
    fun toggleGuess(coord: Pair<Int, Int>, guess: Int) =
        _puzzles.value.lastOrNull()?.toggleGuess(coord, guess)?.let {
            setPuzzle(it)
        }

    /**
     * Remove all entries from the puzzle
     */
    fun clearAllEntries() {
        _puzzles.value.lastOrNull()?.let {
            setPuzzle(it.clearEntries())
        }
    }

    /**
     * Clean up any guesses which contradict entries in the puzzle
     */
    fun cleanGuesses() {
        _puzzles.value.lastOrNull()?.let {
            setPuzzle(it.cleanAllGuesses())
        }
    }

    /**
     * Clear all guesses from the current puzzle
     */
    fun clearAllGuesses() {
        _puzzles.value.lastOrNull()?.let {
            setPuzzle(it.clearGuesses())
        }
    }

    /**
     * Clear everything from the current puzzle
     */
    fun clearAllValues() {
        _puzzles.value.lastOrNull()?.let {
            setPuzzle(it.clearAll())
        }
    }

    /**
     * Set the current puzzle and clear the redo queue
     * @param sudoku The new puzzle to use
     * @return true if puzzle was set, false if not
     */
    fun setPuzzle(sudoku: Sudoku): Boolean {
        return if (sudoku != _puzzles.value.lastOrNull()) {
            _puzzles.value = _puzzles.value + sudoku
            _redoQueue.value = listOf()
            true
        } else false
    }

    /**
     * Toggle the control selection of a square
     * @param square The square to toggle
     */
    fun toggleSquare(square: Pair<Int, Int>) {
        when {
            (square.first < 0) or (square.first > 8) or (square.second < 0) or (square.second > 8) -> Unit
            _puzzles.value.lastOrNull()?.clues?.keys?.let { square in it } ?: false -> Unit
            _controlState.value.selected == square -> _controlState.value =
                _controlState.value.copy(selected = null)
            else -> _controlState.value = _controlState.value.copy(selected = square)
        }
    }

    /**
     * Write the current save to the database
     */
    fun writeCurrentSave() = puzzleSave?.let { ps ->
        _puzzles.value.lastOrNull()?.let {
            puzzleRepository.writeSave(
                ps.copy(
                    clues = it.clues,
                    entries = it.entries.filterValues { it != null }
                        .mapValues { (_, v) -> v!! }.toPersistentMap(),
                    guesses = it.guesses,
                    completed = runBlocking { completed.first() },
                    puzzleTime = timer.time
                )
            )
        }
    }

    /**
     * If there are previous entries in the undo queue, move the current entry to the redo queue
     */
    fun undo() {
        if (_puzzles.value.count() > 1) {
            _puzzles.value.lastOrNull()?.let { last ->
                _redoQueue.value += last
                _puzzles.value = _puzzles.value.dropLast(1)
            }
        }
    }

    /**
     * If there are any entries in the redo queue, move them onto the puzzles stack
     */
    fun redo() {
        _redoQueue.value.lastOrNull()?.let { last ->
            _puzzles.value += last
            _redoQueue.value = _redoQueue.value.dropLast(1)
        }
    }

    /**
     * Vibration manager
     */
    fun vibrate(effect: VibrationEffect) {
        if (vibrationJob?.isActive != true) {
            vibrationJob = viewModelScope.launch(Dispatchers.Default) {
                vibrator.vibrate(effect)
            }
        }
    }

    /**
     * Function to run when the activity is paused. Writes a save and stops the timer
     */
    fun onPause() {
        timer.stopTimer()
        writeCurrentSave()
    }

    /**
     * Function to run when the activity is resumed. Restarts the timer.
     */
    fun onResume() {
        runBlocking {
            if (!completed.first()) timer.startTimer()
        }
    }
}