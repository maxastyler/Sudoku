package com.maxtyler.sudoku.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maxtyler.sudoku.RunFunctionOnPauseAndResume
import com.maxtyler.sudoku.ui.theme.SudokuView

@Composable
fun SudokuScreen(sudokuViewModel: SudokuViewModel = viewModel()) {
    val sudoku by sudokuViewModel.puzzle.collectAsState()
    val contradictions by sudokuViewModel.contradictions.collectAsState(initial = listOf())
    val controlState by sudokuViewModel.controlState.collectAsState()
    val completed by sudokuViewModel.completed.collectAsState(initial = false)

    RunFunctionOnPauseAndResume(onPause = { sudokuViewModel.writeCurrentSave() }, onResume = {})
    Scaffold(topBar = {
        SudokuTopBar(
            playingGame = !completed,
            onClearAllValues = {
                sudokuViewModel.clearAllValues()
                it()
            })
    }) {
        SudokuView(
            sudoku,
            contradictions,
            controlState,
            onCellPressed = { sudokuViewModel.toggleSquare(it) },
            onEntryPressed = { coord, i -> sudokuViewModel.toggleEntry(coord, i) },
            onGuessPressed = { coord, i -> sudokuViewModel.toggleGuess(coord, i) },
            controlsDisabled = completed,
        )
        if (completed) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Card() {
                    Text("Completed :)", modifier = Modifier.padding(20.dp))
                }
            }
        }
    }
}

