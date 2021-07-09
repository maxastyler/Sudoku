package com.maxtyler.sudoku.database

import androidx.room.TypeConverter

class SudokuConverters {
    @TypeConverter
    fun clueToString(clue: Map<Pair<Int, Int>, Int>): String =
        clue.map { (k, v) -> listOf(k.first, k.second, v).joinToString(separator = ",") }
            .joinToString(separator = ",")

    @TypeConverter
    fun stringToClue(string: String): Map<Pair<Int, Int>, Int> =
        string.split(",").chunked(3).filter { it.size == 3 }
            .map { (a, b, c) -> Pair(a.toInt(), b.toInt()) to c.toInt() }
            .toMap()

    @TypeConverter
    fun guessesToString(guess: Map<Pair<Int, Int>, Set<Int>>): String = guess.map { (k, v) ->
        listOf(
            k.first.toString(),
            k.second.toString(),
            v.joinToString(separator = "|")
        ).joinToString(",")
    }.joinToString(";")

    @TypeConverter
    fun stringToGuesses(string: String): Map<Pair<Int, Int>, Set<Int>> =
        string.split(";").mapNotNull {
            try {
                val (x, y, vs) = it.split(",")
                Pair(x.toInt(), y.toInt()) to vs.split("|").map { it.toInt() }.toSet()
            } catch (e: IndexOutOfBoundsException) {
                null
            } catch (e: NumberFormatException) {
                null
            }
        }.toMap()

}