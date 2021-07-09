package com.maxtyler.sudoku.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.maxtyler.sudoku.model.Solver
import com.maxtyler.sudoku.model.Sudoku
import junit.framework.Assert.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PuzzleTest {

    private lateinit var puzzleDao: PuzzleDao
    private lateinit var puzzleSaveDao: PuzzleSaveDao
    private lateinit var db: SudokuDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SudokuDatabase::class.java).build()
        puzzleDao = db.getGeneratedPuzzleDao()
        puzzleSaveDao = db.getPuzzleSaveDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testInsertionAndRetrieval() {
        val toRemove = 1
        val clues = Solver(Sudoku()).findMinSolutions(toRemove)!!
        val puzzle = GeneratedPuzzle(id = 1, clues = clues, minimumClues = 9 * 9 - toRemove)
        runBlocking { puzzleDao.insertGeneratedPuzzle(puzzle) }
        val retrievedPuzzle = runBlocking { puzzleDao.getGeneratedPuzzles().first() }
        assertEquals(listOf(puzzle), retrievedPuzzle)
    }

    @Test
    fun testConvertingAGeneratedPuzzleToSave() {
        val toRemove = 10
        val clues = Solver(Sudoku()).findMinSolutions(toRemove)!!
        val puzzle = GeneratedPuzzle(id = 1, clues = clues, minimumClues = 9 * 9 - toRemove)
        runBlocking { puzzleDao.insertGeneratedPuzzle(puzzle) }
        assertEquals(1, runBlocking { puzzleDao.getGeneratedPuzzles().first() }.size)
        assertFalse(runBlocking {
            puzzleDao.transformGeneratedPuzzleToSave(
                puzzle,
                9 * 9 - toRemove - 1
            )
        })
        assertEquals(1, runBlocking { puzzleDao.getGeneratedPuzzles().first() }.size)
        assertTrue(runBlocking {
            puzzleDao.transformGeneratedPuzzleToSave(
                puzzle,
                9 * 9 - toRemove
            )
        })
        assertEquals(0, runBlocking { puzzleDao.getGeneratedPuzzles().first() }.size)
        assertFalse(runBlocking {
            puzzleDao.transformGeneratedPuzzleToSave(
                puzzle,
                9 * 9 - toRemove
            )
        })
        val saves = runBlocking { puzzleSaveDao.getPuzzles().first() }
        assertEquals(listOf(PuzzleSave(id = puzzle.id,
            clues = puzzle.clues.take(9 * 9 - toRemove).map { (a, b, c) -> Pair(a, b) to c.toInt() }
                .toMap(),
            entries = mapOf(),
            guesses = mapOf()
        )
        ), saves
        )
    }

    @Test
    fun testInsertionAndRetrieval() {
        val puzzle = PuzzleSave(clues = mapOf(), entries = mapOf(), guesses = mapOf())
        runBlocking {
            puzzleDao.insertPuzzle(puzzle)
            puzzleDao.insertPuzzle(puzzle)
            puzzleDao.insertPuzzle(puzzle.copy(id = 1))
        }
        assertEquals(runBlocking() {
            puzzleDao.getPuzzles().first().size
        }, 2)
        val puzzle2 = PuzzleSave(
            id = 1,
            clues = mapOf(2 to 3 to 4, 3 to 4 to 5),
            entries = mapOf(1 to 2 to 3, 2 to 0 to 3),
            guesses = mapOf(2 to 3 to setOf(), 5 to 5 to setOf(2, 3, 4), 1 to 1 to setOf(1))
        )
        runBlocking { puzzleDao.insertPuzzle(puzzle2) }
        assertEquals(puzzle2, runBlocking {
            puzzleDao.getPuzzle(1).first()
        })
    }
}