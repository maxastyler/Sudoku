package com.maxtyler.sudoku.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.maxtyler.sudoku.ui.BoardView
import com.maxtyler.sudoku.ui.ControlState
import com.maxtyler.sudoku.ui.SelectionView
import com.maxtyler.sudoku.ui.SudokuDrawState

@Composable
fun SudokuView(
    sudokuDrawState: SudokuDrawState,
    controlState: ControlState,
    onCellPressed: (Pair<Int, Int>) -> Unit = {},
    onElementToggled: (Pair<Int, Int>, Int) -> Unit = { _, _ -> Unit },
) {
    Column() {
        BoardView(
            sudokuDrawState = sudokuDrawState,
            controlState = controlState,
            onCellPressed = onCellPressed
        )
        SelectionView(selected = controlState.selected?.let {
            when (val x = sudokuDrawState.values.get(controlState.selected)) {
                is SudokuDrawState.SudokuState.Guess -> x.v
                else -> setOf()
            }
        } ?: setOf(),
            controlState.selected?.let {
                { i -> onElementToggled(it, i) }
            } ?: {})
    }
}
