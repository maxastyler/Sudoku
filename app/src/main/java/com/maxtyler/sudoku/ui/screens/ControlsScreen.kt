package com.maxtyler.sudoku.ui.screens

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.maxtyler.sudoku.ui.SudokuTopBar

@Composable
fun ControlsScreen() {
    Scaffold(topBar = { SudokuTopBar(playingGame = false) }) {
        Text("These are the controls")
    }
}