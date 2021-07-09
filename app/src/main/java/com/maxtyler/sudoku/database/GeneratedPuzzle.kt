package com.maxtyler.sudoku.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A generated puzzle, which can be used to play sudoku
 * @param id The puzzle's database id
 * @param clues The list of clues (row, col, element) for the puzzle
 * @param minimumClues The minimum number of clues for which it's
 * guaranteed there is only 1 solution
 */
@Entity
data class GeneratedPuzzle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clues: List<Triple<Int, Int, String>>, val minimumClues: Int
)
