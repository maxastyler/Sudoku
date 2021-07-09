package com.maxtyler.sudoku.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.maxtyler.sudoku.model.Solver
import com.maxtyler.sudoku.model.Sudoku
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GeneratedPuzzleTest {

    private lateinit var generatedPuzzleDao: GeneratedPuzzleDao
    private lateinit var db: SudokuDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SudokuDatabase::class.java).build()
        generatedPuzzleDao = db.getGeneratedPuzzleDao()
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
        generatedPuzzleDao.insertGeneratedPuzzle(puzzle)
        val retrievedPuzzle = runBlocking {generatedPuzzleDao.getGeneratedPuzzles().first()}
        assertEquals(listOf(puzzle), retrievedPuzzle)
    }
}