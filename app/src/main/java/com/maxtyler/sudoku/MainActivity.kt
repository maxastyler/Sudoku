package com.maxtyler.sudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.maxtyler.sudoku.model.Solver
import com.maxtyler.sudoku.model.SolverData
import com.maxtyler.sudoku.model.Sudoku
import com.maxtyler.sudoku.ui.theme.SudokuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SudokuTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    val input = """8 5 . |. . 2 |4 . .
7 2 . |. . . |. . 9
. . 4 |. . . |. . .
------+------+------
. . . |1 . 7 |. . 2
3 . 5 |. . . |9 . .
. 4 . |. . . |. . .
------+------+------
. . . |. 8 . |. 7 .
. 1 7 |. . . |. . .
. . . |. 3 6 |. 4 ."""

    val board = input.split("\n").filter { !it.contains("-") }
        .map { it.replace("|", "").split(" ") }
        .flatMapIndexed { row, v ->
            v.mapIndexed { col, i ->
                Pair(
                    row,
                    col
                ) to i.removeSurrounding(" ")
            }
        }
        .filter { it.second != "." }
        .map { (c, v) -> c to setOf(v.toInt()) }.toMap()

    val r = Solver(Sudoku(board = mapOf()))
    Text(text = r.solve()?.let { Solver.boardToString(it) } ?: "whoopz")

//    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SudokuTheme {
        Greeting("Android")
    }
}