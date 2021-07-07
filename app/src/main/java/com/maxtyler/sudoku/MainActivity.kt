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
import com.maxtyler.sudoku.model.Sudoku
import com.maxtyler.sudoku.ui.SudokuDrawState
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
    val sudoku by sudokuViewModel.puzzleView.collectAsState(initial = SudokuDrawState(Sudoku()))
    val controlState by sudokuViewModel.controlState.collectAsState()
    SudokuView(
        sudoku,
        controlState,
        onCellPressed = { sudokuViewModel.toggleSquare(it) },
        onElementToggled = sudokuViewModel::toggleElement
    )
}