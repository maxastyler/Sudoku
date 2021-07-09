package com.maxtyler.sudoku.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PuzzleSaveTest {
    private lateinit var puzzleDao: PuzzleSaveDao
    private lateinit var db: SudokuDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SudokuDatabase::class.java).build()
        puzzleDao = db.getPuzzleSaveDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        db.close()
    }

    @Test
    fun testInsertionAndRetrieval() {
        val puzzle = PuzzleSave(clues = mapOf(), entries = mapOf(), guesses = mapOf())
        puzzleDao.insertPuzzle(puzzle)
        puzzleDao.insertPuzzle(puzzle)
        puzzleDao.insertPuzzle(puzzle.copy(id = 1))
        assertEquals(runBlocking() {
            puzzleDao.getPuzzles().first().size
        }, 2)
        val puzzle2 = PuzzleSave(
            id = 1,
            clues = mapOf(2 to 3 to 4, 3 to 4 to 5),
            entries = mapOf(1 to 2 to 3, 2 to 0 to 3),
            guesses = mapOf(2 to 3 to setOf(), 5 to 5 to setOf(2, 3, 4), 1 to 1 to setOf(1))
        )
        puzzleDao.insertPuzzle(puzzle2)
        assertEquals(puzzle2, runBlocking {
            puzzleDao.getPuzzle(1).first()
        })
    }
}