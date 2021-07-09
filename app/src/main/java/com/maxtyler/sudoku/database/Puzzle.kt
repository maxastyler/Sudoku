package com.maxtyler.sudoku.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Puzzle(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clues: Map<Pair<Int, Int>, Int>,
    val entries: Map<Pair<Int, Int>, Int>,
    val guesses: Map<Pair<Int, Int>, Set<Int>>
)