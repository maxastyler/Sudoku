package com.maxtyler.sudoku.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
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
import com.maxtyler.sudoku.viewmodels.SudokuViewModel

@Composable
fun SudokuScreen(
    sudokuViewModel: SudokuViewModel = viewModel(),
    onBackToMenu: () -> Unit = {},
    onControlHelp: (() -> Unit) -> Unit = { it() }
) {
    val sudoku by sudokuViewModel.puzzle.collectAsState(null)
    val contradictions by sudokuViewModel.contradictions.collectAsState(initial = listOf())
    val controlState by sudokuViewModel.controlState.collectAsState()
    val completed by sudokuViewModel.completed.collectAsState(initial = false)
    val puzzles by sudokuViewModel.puzzles.collectAsState()
    val redo by sudokuViewModel.redoQueue.collectAsState()

    RunFunctionOnPauseAndResume(onPause = { sudokuViewModel.writeCurrentSave() }, onResume = {})
    Scaffold(topBar = {
        SudokuTopBar(
            playingGame = !completed,
            onClearAllValues = {
                sudokuViewModel.clearAllValues()
                it()
            },
            onClearGuesses = {
                sudokuViewModel.clearAllGuesses()
                it()
            },
            onClearEntries = {
                sudokuViewModel.clearAllEntries()
                it()
            },
            onControls = onControlHelp,
        )
    }) {
        SudokuView(
            sudoku,
            contradictions,
            controlState,
            onCellPressed = { sudokuViewModel.toggleSquare(it) },
            onEntryPressed = { coord, i -> sudokuViewModel.toggleEntry(coord, i) },
            onGuessPressed = { coord, i -> sudokuViewModel.toggleGuess(coord, i) },
            onUndoPressed = sudokuViewModel::undo,
            onRedoPressed = sudokuViewModel::redo,
            undoEnabled = puzzles.count() > 1,
            redoEnabled = redo.count() > 0,
            controlsDisabled = completed,
        )
        if (completed) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Card() {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Woaaaaaah! Nice one :O", modifier = Modifier.padding(20.dp))
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = onBackToMenu) {
                            Text("Back to menu")
                        }
                    }
                }
            }
        }
    }
}

