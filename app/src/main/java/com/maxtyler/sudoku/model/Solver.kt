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

    fun findMinSolutions(toRemove: Int): MutableMap<Pair<Int, Int>, String>? {
        val unset = (1..9).joinToString(separator = "")
        return solve()?.let { solution ->
            val tempSolution = solution.toMutableMap()
            var removed = 0
            while (removed < toRemove) {
                when (val x =
                    tempSolution.filterValues { it.length == 1 }.keys.shuffled().asSequence()
                        .mapNotNull { coord ->
                            if (countSolutions(tempSolution + (coord to unset)) == 1) coord
                            else null
                        }.firstOrNull()) {
                    null -> {
                        break
                    }
                    else -> tempSolution[x] = unset
                }
                removed += 1
            }
            tempSolution
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
