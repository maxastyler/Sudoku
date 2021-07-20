package com.maxtyler.sudoku.database

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Duration
import java.util.*

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

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun fromDuration(value: Duration): String = "${value.seconds}:${value.nano}"

    @TypeConverter
    fun toDuration(value: String): Duration {
        val (s, n) = value.split(":")
        return Duration.ofSeconds(s.toLongOrNull() ?: 0, n.toLongOrNull() ?: 0)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}