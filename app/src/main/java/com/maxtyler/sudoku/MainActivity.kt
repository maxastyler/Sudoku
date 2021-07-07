package com.maxtyler.sudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.maxtyler.sudoku.model.Solver
import com.maxtyler.sudoku.model.Sudoku
import com.maxtyler.sudoku.ui.theme.SudokuDrawState
import com.maxtyler.sudoku.ui.theme.SudokuTheme
import com.maxtyler.sudoku.ui.theme.SudokuView

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

fun getSudoku(): Sudoku {
    val s = Solver(Sudoku())
    var res = s.findMinSolutions(50)
    return Sudoku(clues = res!!.filterValues { it.length == 1 }.mapValues { (_, v) -> v.toInt() })
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
        .map { (c, v) -> c to v.toInt() }.toMap()

//    val s = Sudoku(
//        clues = board, guesses = mapOf(
//            Pair(0, 2) to setOf(4),
//            Pair(0, 3) to setOf(1, 3, 5, 8)
//        )
//    )

//    val r = Solver(Sudoku())
//    r.findMinSolutions()?.let {
//        val s = Sudoku(clues = it.filterValues { it.length == 1 }.mapValues { (_, v) -> v.toInt() })
//        SudokuView(SudokuDrawState(s))
//    }

    val s = getSudoku()
    SudokuView(sudokuDrawState = SudokuDrawState(s))

//    val r = Solver(s)
//    r.findMinSolutions()?.let {
//        Column() {
//            Text(Solver.boardToString(it))
//            Text("${it.count { (_, s) -> s.length == 1 }}")
//        }
//    }
//    Text(text = r.findMinSolutions()?.let { Solver.boardToString(it) } ?: "whoopz")
//    SudokuView(SudokuDrawState(s))
//    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SudokuTheme {
        Greeting("Android")
    }
}