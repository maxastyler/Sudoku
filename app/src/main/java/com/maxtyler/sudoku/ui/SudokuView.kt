package com.maxtyler.sudoku.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.maxtyler.sudoku.model.Sudoku
import com.maxtyler.sudoku.ui.BoardView
import com.maxtyler.sudoku.ui.ControlState
import com.maxtyler.sudoku.ui.SelectionView

@Composable
fun SudokuView(
    sudoku: Sudoku,
    contradictions: List<Pair<Int, Int>>,
    controlState: ControlState,
    onCellPressed: (Pair<Int, Int>) -> Unit = {},
    onEntryPressed: (Pair<Int, Int>, Int) -> Unit = { _, _ -> Unit },
    onGuessPressed: (Pair<Int, Int>, Int) -> Unit = { _, _ -> Unit },
) {
    Column() {
        BoardView(
            sudoku = sudoku,
            contradictions = contradictions,
            controlState = controlState,
            onCellPressed = onCellPressed,
        )
        val (coord, entry, guess) = controlState.selected?.let { coord ->
            Triple(
                coord,
                sudoku.entries[coord],
                sudoku.guesses[coord] ?: setOf()
            )
        } ?: Triple(null, null, setOf())
        SelectionView(entry = entry,
            guess = guess,
            onEntryPressed = coord?.let { { i -> onEntryPressed(coord, i) } } ?: {},
            onGuessPressed = coord?.let { { i -> onGuessPressed(coord, i) } } ?: {})
    }
}
