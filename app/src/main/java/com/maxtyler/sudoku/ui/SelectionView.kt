package com.maxtyler.sudoku.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

sealed class SelectionType {
    data class Entry(val x: Int) : SelectionType()
    data class Guess(val x: Set<Int>) : SelectionType()
}

@Composable
fun NumberGrid(selected: Set<Int>, onItemPressed: (Int) -> Unit = {}) {
    Row(modifier = Modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
        (0..2).forEach { col ->
            Column() {
                (0..2).forEach { row ->
                    val x = row * 3 + col + 1
                    Button(
                        onClick = { onItemPressed(x) },
                        colors = ButtonDefaults.buttonColors(backgroundColor = if (x in selected) Color.Gray else Color.LightGray)
                    ) {
                        Text(x.toString())
                    }
                }
            }
        }
    }
}

@Composable
fun SelectionView(
    entry: Int?,
    guess: Set<Int>,
    onEntryPressed: (Int) -> Unit = {},
    onGuessPressed: (Int) -> Unit = {}
) {
    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.SpaceBetween) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Entry")
            NumberGrid(entry?.let { setOf(it) } ?: setOf(), onEntryPressed)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Guess")
            NumberGrid(guess, onGuessPressed)
        }
    }
}