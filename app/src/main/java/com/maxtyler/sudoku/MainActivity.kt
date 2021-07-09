package com.maxtyler.sudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maxtyler.sudoku.ui.SudokuViewModel
import com.maxtyler.sudoku.ui.theme.SudokuTheme
import com.maxtyler.sudoku.ui.theme.SudokuView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SudokuTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Main()
                }
            }
        }
    }
}

@Composable
fun Main(sudokuViewModel: SudokuViewModel = viewModel()) {
    val sudoku by sudokuViewModel.puzzle.collectAsState()
    val contradictions by sudokuViewModel.contradictions.collectAsState(initial = listOf())
    val controlState by sudokuViewModel.controlState.collectAsState()
    SudokuView(
        sudoku,
        contradictions,
        controlState,
        onCellPressed = { sudokuViewModel.toggleSquare(it) },
        onEntryPressed = { coord, i -> sudokuViewModel.toggleEntry(coord, i) },
        onGuessPressed = { coord, i -> sudokuViewModel.toggleGuess(coord, i) },
    )
}