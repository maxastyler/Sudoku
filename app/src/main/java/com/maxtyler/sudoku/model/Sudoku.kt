package com.maxtyler.sudoku.model

data class Sudoku(
    val clues: Map<Pair<Int, Int>, Int> = mapOf(),
    val entries: Map<Pair<Int, Int>, Int?> = mapOf(),
    val guesses: Map<Pair<Int, Int>, Set<Int>> = mapOf()
) {
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

    companion object {
        private fun inBounds(coord: Pair<Int, Int>): Boolean =
            (coord.first >= 0) and (coord.first <= 8) and (coord.second >= 0) and (coord.second <= 8)
    }

}
