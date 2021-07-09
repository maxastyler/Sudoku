package com.maxtyler.sudoku.database

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SudokuConverters {
    private val json = Json { allowStructuredMapKeys = true }

    @TypeConverter
    fun clueToString(clue: Map<Pair<Int, Int>, Int>): String = json.encodeToString(clue)

    @TypeConverter
    fun stringToClue(string: String): Map<Pair<Int, Int>, Int> = json.decodeFromString(string)

    @TypeConverter
    fun guessesToString(guess: Map<Pair<Int, Int>, Set<Int>>): String = json.encodeToString(guess)

    @TypeConverter
    fun stringToGuesses(string: String): Map<Pair<Int, Int>, Set<Int>> =
        json.decodeFromString(string)

    @TypeConverter
    fun saveListToString(save: List<Triple<Int, Int, String>>): String = json.encodeToString(save)

    @TypeConverter
    fun stringToSaveList(string: String): List<Triple<Int, Int, String>> =
        json.decodeFromString(string)
}