package com.maxtyler.sudoku.database

import com.maxtyler.sudoku.model.Solver
import com.maxtyler.sudoku.model.Sudoku
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNotSame
import kotlinx.collections.immutable.toPersistentMap
import org.junit.Test

class GeneratedPuzzleTest {

    private fun solvePuzzle(
        generatedPuzzle: GeneratedPuzzle,
        toRemove: Int
    ): Map<Pair<Int, Int>, String>? {
        return Solver(Sudoku(generatedPuzzle.clues.take(9 * 9 - toRemove)
            .map { (row, col, v) -> (row to col) to v.toInt() }
            .toMap().toPersistentMap())).solve()
    }

    @Test
    fun shuffle() {
        val toRemove = 40
        var puzzle: List<Triple<Int, Int, String>>? = null
        while (puzzle == null) {
            Solver(Sudoku()).findMinSolutions(toRemove)?.let { puzzle = it }
        }
        val p = GeneratedPuzzle(clues = puzzle!!, minimumClues = 9 * 9 - 3)
        assertNotSame(p.shuffle(),p)
        assertNotSame(p.shuffle(),p)
        assertNotNull(solvePuzzle(p.shuffle(), toRemove))
        assertNotNull(solvePuzzle(p.shuffle(), toRemove))
        assertNotNull(solvePuzzle(p.shuffle(), toRemove))
        assertNotNull(solvePuzzle(p.shuffle(), toRemove))
    }
}