package com.maxtyler.sudoku.model

data class Sudoku(
    val clues: Map<Pair<Int, Int>, Int> = mapOf(),
    val guesses: Map<Pair<Int, Int>, Set<Int>> = mapOf()
) {
    fun toggleElement(coord: Pair<Int, Int>, element: Int): Sudoku? =
        when {
            (coord.first < 0) or (coord.first > 8) or (coord.second < 0) or (coord.second > 8) or (coord in clues.keys) -> null
            else -> {
                val coordSet = guesses.getOrDefault(coord, setOf())
                this.copy(
                    guesses = guesses + (coord to
                            (if (element in coordSet) {
                                coordSet - element
                            } else {
                                coordSet + element
                            }))
                )
            }
        }

}
