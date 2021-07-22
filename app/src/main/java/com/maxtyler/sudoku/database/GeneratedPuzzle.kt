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
) {
    /**
     * Return a new puzzle which is this one with rows and columns shuffled and elements permuted
     */
    fun shuffle(): GeneratedPuzzle {
        var newPuzzle: List<Triple<Int, Int, String>>? = null
        do {
            val rowShuffle = (0 until 9).chunked(3).shuffled().flatMap { it.shuffled() }
            val colShuffle = (0 until 9).chunked(3).shuffled().flatMap { it.shuffled() }
            val elementShuffle = (1..9).shuffled().map { it.toString() }
            newPuzzle = clues.map { (row, col, v) ->
                Triple(
                    rowShuffle[row],
                    colShuffle[col],
                    elementShuffle[v.toInt() - 1]
                )
            }
        } while (newPuzzle == this.clues)
        return GeneratedPuzzle(clues = newPuzzle!!, minimumClues = this.minimumClues)
    }
}