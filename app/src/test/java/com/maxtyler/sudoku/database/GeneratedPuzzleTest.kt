package com.maxtyler.sudoku.database

import com.maxtyler.sudoku.model.Solver
import com.maxtyler.sudoku.model.Sudoku
import junit.framework.Assert.*
import kotlinx.collections.immutable.toPersistentMap
import org.junit.Test

class GeneratedPuzzleTest {

    private fun solvePuzzle(
        generatedPuzzle: GeneratedPuzzle,
        toRemove: Int
    ): Map<Pair<Int, Int>, String>? {
        return generatedPuzzle.clues.take(9 * 9 - toRemove)
            .map { (row, col, v) -> (row to col) to v }.toMap().let {
                if (Solver.isUnique(it)) it else null
            }?.mapValues { (_, v) -> v.toInt() }?.toPersistentMap()
            ?.let { Solver(Sudoku(it)).solve() }
    }

    @Test
    fun shuffle() {
        val toRemove = 45
        var puzzle: List<Triple<Int, Int, String>>? = null
        while (puzzle == null) {
            Solver(Sudoku()).findMinSolutions(toRemove)?.let { puzzle = it }
        }
        val p = GeneratedPuzzle(clues = puzzle!!, minimumClues = 9 * 9 - toRemove)
        assertNotSame(p.shuffle(), p)
        assertNotSame(p.shuffle(), p)
        (0 until 100).forEach {
            assertNotNull(solvePuzzle(p.shuffle(), toRemove))
        }
    }

    @Test
    fun testIsUnique() {
        val p = Sudoku()
        val solver = Solver(p).solve()
        assertFalse(Solver.isUnique((0 until 4).flatMap { x -> (0 until 9).map { Pair(x, it) } }
            .fold(solver!!, { acc, v -> acc + (v to "123456789") })))
    }
}