package com.maxtyler.sudoku.model

import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import org.junit.Assert.*
import org.junit.Test

class SudokuTest {

    val s = Sudoku()

    @Test
    fun completed() {
        assertFalse(s.completed())
        val solved = Sudoku(clues = Solver(s).solve()?.mapValues { (_, v) -> v.toInt() + 1 }
            ?.toPersistentMap() ?: persistentMapOf())
        assertTrue(solved.completed())
    }

    @Test
    fun toggleEntry() {
        assertNull(s.entries[0 to 0])
        assertEquals(3, s.toggleEntry(0 to 0, 3)?.entries?.get(0 to 0))
        assertEquals(4, s.toggleEntry(0 to 0, 3)?.toggleEntry(0 to 0, 4)?.entries?.get(0 to 0))
        assertNull(s.toggleEntry(0 to 0, 3)?.toggleEntry(0 to 0, 3)?.entries?.get(0 to 0))
        assertNull(s.toggleEntry(0 to -1, 3))
    }

    @Test
    fun toggleGuess() {
        assertNull(s.guesses[0 to 0])
        assertEquals(setOf(3), s.toggleGuess(0 to 0, 3)?.guesses?.get(0 to 0))
        assertEquals(
            setOf(2, 3),
            s.toggleGuess(0 to 0, 3)?.toggleGuess(0 to 0, 2)?.toggleGuess(0 to 0, 1)
                ?.toggleGuess(0 to 0, 1)?.guesses?.get(0 to 0)
        )
    }

    @Test
    fun clearGuess() {
        assertEquals(
            persistentMapOf<Pair<Int, Int>, Set<Int>>(),
            s.toggleGuess(0 to 0, 3)?.clearGuess(0 to 0)?.guesses
        )
    }

    @Test
    fun cleanAllGuesses() {
        val newS = s.toggleGuess(0 to 0, 3)?.toggleGuess(0 to 0, 4)?.toggleEntry(0 to 1, 3)!!
        assertEquals(newS.toggleGuess(0 to 0, 3)!!, newS.cleanAllGuesses())
    }

    @Test
    fun clearGuesses() {
        assertEquals(
            persistentMapOf<Pair<Int, Int>, Set<Int>>(),
            s.toggleGuess(0 to 0, 3)?.toggleGuess(0 to 1, 4)?.toggleGuess(3 to 3, 2)
                ?.toggleGuess(3 to 3, 3)?.clearGuesses()?.guesses
        )
    }

    @Test
    fun clearEntries() {
        assertEquals(
            s.entries,
            s.toggleEntry(0 to 0, 3)?.toggleEntry(0 to 1, 2)?.toggleEntry(4 to 4, 5)
                ?.clearEntries()?.entries
        )
    }

    @Test
    fun clearAll() {
        val newS = s.toggleEntry(0 to 0, 3)?.toggleEntry(2 to 2, 4)?.toggleGuess(0 to 1, 3)
            ?.toggleGuess(0 to 1, 4)?.clearAll()
        assertEquals(
            s.entries, newS?.entries
        )
        assertEquals(s.guesses, newS?.guesses)
    }

    @Test
    fun findContradictions() {
        assertEquals(0, s.findContradictions().count())
        assertEquals(
            2,
            s.toggleEntry(0 to 0, 1)?.toggleEntry(0 to 1, 1)?.findContradictions()?.count()
        )
    }
}