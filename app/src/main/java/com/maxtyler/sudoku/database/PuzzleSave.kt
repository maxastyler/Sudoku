package com.maxtyler.sudoku.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Duration
import java.util.*

@Entity
data class PuzzleSave(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clues: Map<Pair<Int, Int>, Int>,
    val entries: Map<Pair<Int, Int>, Int>,
    val guesses: Map<Pair<Int, Int>, Set<Int>>,
    val dateWritten: Date,
    val puzzleTime: Duration,
    val completed: Boolean = false,
)