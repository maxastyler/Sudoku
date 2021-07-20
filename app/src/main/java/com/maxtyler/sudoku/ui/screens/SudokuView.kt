package com.maxtyler.sudoku.ui.theme

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maxtyler.sudoku.model.Sudoku
import com.maxtyler.sudoku.ui.BoardView
import com.maxtyler.sudoku.ui.screens.SelectionView
import com.maxtyler.sudoku.ui.utils.ControlState
import com.maxtyler.sudoku.ui.utils.stringFormat
import java.time.Duration

@ExperimentalFoundationApi
@Composable
fun SudokuView(
    sudoku: Sudoku?,
    contradictions: List<Pair<Int, Int>>,
    controlState: ControlState,
    onCellPressed: (Pair<Int, Int>) -> Unit = {},
    onEntryPressed: (Pair<Int, Int>, Int) -> Unit = { _, _ -> Unit },
    onGuessPressed: (Pair<Int, Int>, Int) -> Unit = { _, _ -> Unit },
    onControlHelp: () -> Unit = {},
    onUndoPressed: () -> Unit = {},
    onRedoPressed: () -> Unit = {},
    onCleanGuessesPressed: () -> Unit = {},
    undoEnabled: Boolean = true,
    redoEnabled: Boolean = true,
    controlsDisabled: Boolean = false,
    time: Duration? = null,
) {
    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        BoardView(
            sudoku = sudoku,
            contradictions = contradictions,
            controlState = controlState,
            onCellPressed = onCellPressed,
            controlsDisabled = controlsDisabled,
        )
        Spacer(modifier = Modifier.height(5.dp))
        if (!controlsDisabled) {
            val (coord, entry, guess) = sudoku?.let { s ->
                controlState.selected?.let { coord ->
                    Triple(
                        coord,
                        sudoku.entries[coord],
                        sudoku.guesses[coord] ?: setOf()
                    )
                }
            } ?: Triple(null, null, setOf())
            SelectionView(
                entry = entry,
                guess = guess,
                onEntryPressed = coord?.let { { i -> onEntryPressed(coord, i) } } ?: {},
                onGuessPressed = coord?.let { { i -> onGuessPressed(coord, i) } } ?: {},
                onUndoPressed = onUndoPressed,
                onRedoPressed = onRedoPressed,
                onControlHelpPressed = onControlHelp,
                undoEnabled = undoEnabled,
                redoEnabled = redoEnabled,
                onCleanGuessesPressed = onCleanGuessesPressed,
            )
        }
    }
}
