package com.maxtyler.sudoku.model

import android.util.Log

data class Solver(val board: Map<Pair<Int, Int>, String>) {
    constructor(sudoku: Sudoku) : this(board = (0..8).flatMap { row ->
        (0..8).map { col ->
            (row to col) to sudoku.clues.mapValues { (_, v) -> setOf(v) }.getOrDefault(
                row to col,
                sudoku.guesses.getOrDefault(row to col, (1..9).toSet())
            ).joinToString("")
        }
    }.toMap())

    override fun toString() = Solver.boardToString(board)

    fun solve(): Map<Pair<Int, Int>, String>? {
        val map = board.toMutableMap()
        return search(map)
    }

    /**
     * Find the minimum solutions for the current board, and return the solutions
     * in a list, where the first `toRemove` elements create a map which
     * is solveable, and each element after can be added to the map to decrease the difficulty
     */
    fun findMinSolutions(toRemove: Int): List<Triple<Int, Int, String>>? {
        val unset = (1..9).joinToString(separator = "")
        val clues: MutableList<Triple<Int, Int, String>> = mutableListOf()
        return solve()?.let { solution ->
            val tempSolution = solution.toMutableMap()
            var removed = 0
            for (coord in solution.filterValues { it.length == 1 }.keys.shuffled()) {
                Log.d("GAMES", "Removed: ${removed}")
                if (isUnique(tempSolution + (coord to unset))) {
                    clues.add(Triple(coord.first, coord.second, tempSolution[coord]!!))
                    tempSolution[coord] = unset
                    removed += 1
                    if (removed >= toRemove) break
                }
            }
            if (removed < toRemove) null
            else {
                tempSolution.filterValues { v -> v.length == 1 }
                    .forEach { (k, v) -> clues.add(Triple(k.first, k.second, v)) }
                clues.reversed()
            }
        }
    }

    companion object {
        fun eliminate(
            values: MutableMap<Pair<Int, Int>, String>,
            s: Pair<Int, Int>,
            d: Char
        ): MutableMap<Pair<Int, Int>, String>? {
            if (!(values[s]?.contains(d) ?: false)) return values
            values[s] = values[s]!!.replace(d.toString(), "")
            when (values[s]!!.length) {
                0 -> return null
                1 -> {
                    val d2 = values[s]!!.first()
                    if (!neighbours[s]!!.all { s2 ->
                            eliminate(
                                values,
                                s2,
                                d2
                            ) != null
                        }) return null
                }
            }
            units[s]?.forEach { u ->
                val dPlaces = u.filter { k -> values[k]?.contains(d) ?: false }
                when (dPlaces.size) {
                    0 -> return null
                    1 -> if (assign(values, dPlaces.first(), d) == null) return null
                }
            }
            return values
        }

        fun assign(
            values: MutableMap<Pair<Int, Int>, String>,
            s: Pair<Int, Int>,
            d: Char
        ): MutableMap<Pair<Int, Int>, String>? {
            val otherValues = values[s]!!.replace(d.toString(), "")
            return if (otherValues.all { eliminate(values, s, it) != null }) {
                values
            } else null
        }

        fun search(values: MutableMap<Pair<Int, Int>, String>?): MutableMap<Pair<Int, Int>, String>? {
            if (values == null) return null
            if (values.all { (_, v) -> v.length == 1 }) return values
            val (k, v) = values.toList().filter { (_, v) -> v.length > 1 }.shuffled()
                .minByOrNull { (_, v) -> v.length }!!
            v.toList().shuffled().forEach {
                search(assign(values.toMutableMap(), k, it))?.let {
                    return it
                }
            }
            return null
        }

        fun isUnique(values: Map<Pair<Int, Int>, String>): Boolean =
            isUniqueInner(values.toMutableMap())

        private fun isUniqueInner(values: MutableMap<Pair<Int, Int>, String>?): Boolean {
            if (values == null) return false
            when (val mini = values.toList().filter { (_, v) -> v.length > 1 }
                .minByOrNull { (_, v) -> v.length }) {
                null -> return true // the filter returned no elements, meaning all elements have been placed
                else -> {
                    var solCount = 0
                    for (c in mini.second) {
                        if (isUniqueInner(
                                assign(
                                    values.toMutableMap(),
                                    mini.first,
                                    c
                                )
                            )
                        ) solCount += 1
                        if (solCount > 1) break
                    }
                    return (solCount == 1)
                }
            }
        }

        fun countSolutions(values: Map<Pair<Int, Int>, String>): Int =
            countSolutionsInner(values.toMutableMap())

        private fun countSolutionsInner(values: MutableMap<Pair<Int, Int>, String>?): Int {
            if (values == null) return 0
            if (values.all { (_, v) -> v.length == 1 }) return 1
            val (k, v) = values.toList().filter { (_, v) -> v.length > 1 }
                .minByOrNull { (_, v) -> v.length }!!
            return v.fold(0) { acc, c ->
                acc + countSolutionsInner(
                    assign(
                        values.toMutableMap(),
                        k,
                        c
                    )
                )
            }
        }

        fun boardToString(board: Map<Pair<Int, Int>, String>): String {
            val widths = (0..8).map { col ->
                (0..8).map { row -> board[row to col] }.maxOf { it?.length ?: 0 }
            }
            val strings =
                (0..8).flatMap { row -> (0..8).map { col -> board.getOrDefault(row to col, "") } }
            return strings.chunked(9)
                .map {
                    widths.zip(it).map { (w, s) -> s.padStart(w) }.chunked(3)
                        .map { it.joinToString(separator = " ") }.joinToString(separator = " | ")
                }
                .chunked(3).map { it.joinToString(separator = "\n") }.joinToString("\n\n")
        }

        private val rows =
            (0 until 9).map { row ->
                (0 until 9).map { col -> row to col }
            }
        private val cols =
            (0 until 9).map { col ->
                (0 until 9).map { row -> row to col }
            }

        private val squares =
            (0 until 9 step 3).flatMap { row ->
                (0 until 9 step 3).map { col ->
                    (0 until 3)
                        .flatMap { innerRow ->
                            (0 until 3).map { innerCol ->
                                (row + innerRow) to (col + innerCol)
                            }
                        }
                }
            }

        private val unitList = rows + cols + squares

        /**
         * An array from indices in the solution to a list of unit indices
         */
        val units: Map<Pair<Int, Int>, List<List<Pair<Int, Int>>>> = (0 until 9).flatMap { row ->
            (0 until 9).map { col ->
                Pair(row, col) to unitList.filter { Pair(row, col) in it }
            }
        }.toMap()

        /**
         * An array from indices in the solution to a list of indices which neighbour that index
         */
        val neighbours: Map<Pair<Int, Int>, List<Pair<Int, Int>>> =
            units.mapValues { (k, v) -> v.flatten().distinct().filter { it != k } }
    }
}
