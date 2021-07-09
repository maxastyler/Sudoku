package com.maxtyler.sudoku.repository

import com.maxtyler.sudoku.model.Solver
import com.maxtyler.sudoku.model.Sudoku

class PuzzleRepository {
    suspend fun getPuzzle(numberFilled: Int): Sudoku {
        var puzzle: Map<Pair<Int, Int>, String>? = null
        while (puzzle == null) {
            Solver(Sudoku()).findMinSolutions(81 - numberFilled)
                ?.let { puzzle = it.take(numberFilled).map { (a, b, c) -> (a to b) to c }.toMap() }
        }
        return Sudoku(clues = puzzle!!.filterValues { v -> v.length == 1 }
            .mapValues { (_, v) -> v.toInt() })
    }
}