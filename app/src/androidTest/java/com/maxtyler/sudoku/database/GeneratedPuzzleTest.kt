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
class GeneratedPuzzleTest {

    private lateinit var generatedPuzzleDao: GeneratedPuzzleDao
    private lateinit var puzzleSaveDao: PuzzleSaveDao
    private lateinit var db: SudokuDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SudokuDatabase::class.java).build()
        generatedPuzzleDao = db.getGeneratedPuzzleDao()
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
        runBlocking { generatedPuzzleDao.insertGeneratedPuzzle(puzzle) }
        val retrievedPuzzle = runBlocking { generatedPuzzleDao.getGeneratedPuzzles().first() }
        assertEquals(listOf(puzzle), retrievedPuzzle)
    }

    @Test
    fun testConvertingAGeneratedPuzzleToSave() {
        val toRemove = 10
        val clues = Solver(Sudoku()).findMinSolutions(toRemove)!!
        val puzzle = GeneratedPuzzle(id = 1, clues = clues, minimumClues = 9 * 9 - toRemove)
        runBlocking { generatedPuzzleDao.insertGeneratedPuzzle(puzzle) }
        assertEquals(1, runBlocking { generatedPuzzleDao.getGeneratedPuzzles().first() }.size)
        assertFalse(runBlocking {
            generatedPuzzleDao.transformGeneratedPuzzleToSave(
                puzzle,
                9 * 9 - toRemove - 1
            )
        })
        assertEquals(1, runBlocking { generatedPuzzleDao.getGeneratedPuzzles().first() }.size)
        assertTrue(runBlocking {
            generatedPuzzleDao.transformGeneratedPuzzleToSave(
                puzzle,
                9 * 9 - toRemove
            )
        })
        assertEquals(0, runBlocking { generatedPuzzleDao.getGeneratedPuzzles().first() }.size)
        assertFalse(runBlocking {
            generatedPuzzleDao.transformGeneratedPuzzleToSave(
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
}