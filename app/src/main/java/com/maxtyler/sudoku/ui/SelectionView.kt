package com.maxtyler.sudoku.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SelectionView(selected: Set<Int>, onItemPressed: (Int) -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        (0..2).forEach { col ->
            Column() {
                (0..2).forEach { row ->
                    val x = row * 3 + col + 1
                    Button(onClick = { onItemPressed(x) }) {
                        Text(x.toString())
                    }
                }
            }
        }
    }
}