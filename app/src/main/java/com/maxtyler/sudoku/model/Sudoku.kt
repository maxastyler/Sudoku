package com.maxtyler.sudoku.model

data class Sudoku(
    val clues: Map<Pair<Int, Int>, Int> = mapOf(),
    val guesses: Map<Pair<Int, Int>, Set<Int>> = mapOf()
) {
}
