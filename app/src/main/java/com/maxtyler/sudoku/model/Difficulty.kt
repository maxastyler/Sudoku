package com.maxtyler.sudoku.model

/**
 * The different difficulty settings
 * @param clues The number of clues this difficulty setting has in the puzzle
 */
enum class Difficulty(val clues: Int) {
    Easy(50), Medium(40), Hard(30), Desperate(25),
}