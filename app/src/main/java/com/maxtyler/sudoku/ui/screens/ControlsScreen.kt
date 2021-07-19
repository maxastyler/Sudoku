package com.maxtyler.sudoku.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maxtyler.sudoku.ui.SudokuTopBar

@Composable
fun ControlsScreen() {
    Scaffold(topBar = { SudokuTopBar(playingGame = false) }) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp), contentAlignment = Alignment.Center
        ) {
            Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                Text("It's sudoku, dummy!", fontStyle = FontStyle.Italic, fontSize = 30.sp)
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    """
            Select an empty space by tapping on it, and then use the numpad to enter in possible guesses.
            When you're sure of what goes in a square, long press the number on the numpad to enter it.
            
            You can undo and redo with the undo and redo buttons :^O
            
            Use the "clean guesses" button to clean up guesses when you've confirmed a number.
        """.trimIndent()
                )
            }
        }
    }
}