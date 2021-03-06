package com.maxtyler.sudoku.ui.utils

import com.maxtyler.sudoku.model.Sudoku

data class SudokuDrawState(val values: Map<Pair<Int, Int>, SudokuState>) {

    constructor(sudoku: Sudoku) : this(values = (0..8).flatMap { row ->
        (0..8).map { col ->
            val coord = Pair(row, col)
            Pair(coord,
                sudoku.clues[coord]?.let {
                    SudokuState.Clue(it)
                } ?: SudokuState.UserEntry(
                    entry = sudoku.entries[coord],
                    guess = sudoku.guesses[coord].orEmpty()
                ))
        }
    }.toMap())

    sealed class SudokuState {
        data class Clue(val v: Int) : SudokuState()
        data class UserEntry(val entry: Int?, val guess: Set<Int>) : SudokuState()
    }
}
