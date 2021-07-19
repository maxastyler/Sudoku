package com.maxtyler.sudoku.model

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.plus

data class Sudoku(
    val clues: PersistentMap<Pair<Int, Int>, Int> = persistentMapOf(),
    val entries: PersistentMap<Pair<Int, Int>, Int?> = persistentMapOf(),
    val guesses: PersistentMap<Pair<Int, Int>, Set<Int>> = persistentMapOf(),
) {
    fun completed(): Boolean =
        ((entries.filterValues { it != null } + clues).count() >= 81) and findContradictions().isEmpty()

    fun toggleEntry(coord: Pair<Int, Int>, entry: Int): Sudoku? = when {
        !inBounds(coord) or (coord in clues.keys) -> null
        entries[coord] == entry -> copy(entries = entries + (coord to null))
        else -> copy(entries = entries + (coord to entry))
    }

    fun toggleGuess(coord: Pair<Int, Int>, guess: Int): Sudoku? =
        when {
            !inBounds(coord) or (coord in clues.keys) -> null
            else -> {
                val coordSet = guesses.getOrDefault(coord, setOf())
                this.copy(
                    guesses = guesses + (coord to
                            (if (guess in coordSet) {
                                coordSet - guess
                            } else {
                                coordSet + guess
                            }))
                )
            }
        }

    fun clearGuess(coord: Pair<Int, Int>): Sudoku = copy(guesses = this.guesses - coord)

    fun clearGuesses(): Sudoku = copy(guesses = persistentMapOf())
    fun clearEntries(): Sudoku = copy(entries = persistentMapOf())
    fun clearAll(): Sudoku = this.clearGuesses().clearEntries()

    fun findContradictions(): List<Pair<Int, Int>> = this.entries.mapNotNull { (coord, entry) ->
        if (Solver.neighbours[coord]?.any { c -> (this.clues[c] == entry) or (this.entries[c] == entry) }
                ?: false) coord else null
    }

    companion object {
        private fun inBounds(coord: Pair<Int, Int>): Boolean =
            (coord.first >= 0) and (coord.first <= 8) and (coord.second >= 0) and (coord.second <= 8)
    }
}
