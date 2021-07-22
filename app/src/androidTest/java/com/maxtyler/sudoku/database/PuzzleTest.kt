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
import java.time.Duration
import java.time.Instant
import java.util.*

@RunWith(AndroidJUnit4::class)
class PuzzleTest {

    private lateinit var puzzleDao: PuzzleDao
    private lateinit var db: SudokuDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, SudokuDatabase::class.java).build()
        puzzleDao = db.getGeneratedPuzzleDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testGeneratedPuzzleInsertionAndRetrieval() {
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
        assertNull(runBlocking {
            puzzleDao.transformGeneratedPuzzleToSave(
                puzzle,
                9 * 9 - toRemove - 1
            )?.first()
        })
        assertEquals(1, runBlocking { puzzleDao.getGeneratedPuzzles().first() }.size)
        assertNotNull(runBlocking {
            puzzleDao.transformGeneratedPuzzleToSave(
                puzzle,
                9 * 9 - toRemove
            )?.first()
        })
        assertEquals(0, runBlocking { puzzleDao.getGeneratedPuzzles().first() }.size)
        assertNull(runBlocking {
            puzzleDao.transformGeneratedPuzzleToSave(
                puzzle,
                9 * 9 - toRemove
            )?.first()
        })
        val saves = runBlocking { puzzleDao.getPuzzleSavesByCompletion(0).first() }
        assertEquals(listOf(PuzzleSave(
            id = puzzle.id,
            clues = puzzle.clues.take(9 * 9 - toRemove).map { (a, b, c) -> Pair(a, b) to c.toInt() }
                .toMap(),
            entries = mapOf(),
            guesses = mapOf(),
            dateWritten = saves.firstOrNull()?.dateWritten ?: Date.from(Instant.now()),
            puzzleTime = Duration.ZERO,
        )
        ), saves
        )
    }

    @Test
    fun testPuzzleSaveInsertionAndRetrieval() {
        val date = Date.from(Instant.now())
        val puzzle = PuzzleSave(
            clues = mapOf(),
            entries = mapOf(),
            guesses = mapOf(),
            dateWritten = date,
            puzzleTime = Duration.ZERO,
        )
        runBlocking {
            puzzleDao.insertPuzzleSave(puzzle)
            puzzleDao.insertPuzzleSave(puzzle)
            assertEquals(1, puzzleDao.insertPuzzleSave(puzzle.copy(id = 1)))
        }
        assertEquals(runBlocking {
            puzzleDao.getPuzzleSavesByCompletion(0).first().size
        }, 2)
        val puzzle2 = PuzzleSave(
            id = 1,
            clues = mapOf(2 to 3 to 4, 3 to 4 to 5),
            entries = mapOf(1 to 2 to 3, 2 to 0 to 3),
            guesses = mapOf(2 to 3 to setOf(), 5 to 5 to setOf(2, 3, 4), 1 to 1 to setOf(1)),
            dateWritten = date,
            puzzleTime = Duration.ZERO,
        )
        runBlocking { puzzleDao.insertPuzzleSave(puzzle2) }
        assertEquals(puzzle2, runBlocking {
            puzzleDao.getPuzzleSave(1).first()
        })
    }

    @Test
    fun testCountingGeneratedPuzzles() {
        val cluesToRemove = 3
        val thingsToAdd = 4
        assertEquals(
            0,
            runBlocking { puzzleDao.generatedPuzzleCount(9 * 9 - cluesToRemove).first() })
        runBlocking {
            (1..thingsToAdd).forEach {
                puzzleDao.insertGeneratedPuzzle(
                    GeneratedPuzzle(
                        clues = Solver(Sudoku()).findMinSolutions(
                            cluesToRemove
                        )!!,
                        minimumClues = 9 * 9 - cluesToRemove
                    )
                )
            }
        }
        assertEquals(
            thingsToAdd,
            runBlocking { puzzleDao.generatedPuzzleCount(9 * 9 - cluesToRemove).first() })
    }

    @Test
    fun testGetFirstGeneratedPuzzle() {
        val cluesToRemove = 3
        assertNull(runBlocking {
            puzzleDao.getFirstGeneratedPuzzleFlow(minimumClues = 9 * 9 - cluesToRemove).first()
        })
        runBlocking {
            (1..4).forEach {
                puzzleDao.insertGeneratedPuzzle(
                    GeneratedPuzzle(
                        clues = Solver(Sudoku()).findMinSolutions(
                            cluesToRemove
                        )!!,
                        minimumClues = 9 * 9 - cluesToRemove
                    )
                )
            }
        }
        assertNotNull(runBlocking { puzzleDao.getFirstGeneratedPuzzleFlow(9 * 9 - cluesToRemove) })
    }
}