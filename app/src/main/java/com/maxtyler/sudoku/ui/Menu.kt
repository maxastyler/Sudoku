package com.maxtyler.sudoku.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.maxtyler.sudoku.database.PuzzleSave
import com.maxtyler.sudoku.model.Sudoku
import kotlinx.coroutines.launch

@Composable
fun Menu(menuViewModel: MenuViewModel = viewModel(), navHostController: NavHostController) {
    val saves by menuViewModel.saves.collectAsState(initial = listOf())
    val listState = rememberLazyListState()
    val saveCount by menuViewModel.puzzleCount.collectAsState(0)

    var scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(), onClick = {
            scope.launch {
                menuViewModel.createPuzzle()?.let {
                    navHostController.navigate("game/${it}/30")
                }
            }
        }, enabled = (saveCount > 0)) {
            if (saveCount > 0) Text("New Game")
            else Text("Waiting for game to generate puzzles...")
        }

        LazyColumn(state = listState) {

            items(saves) {
                Box(modifier = Modifier.clickable { Log.d("GAMES", "Hi") }) {
                    SaveView(it, onClicked = {
                        navHostController.navigate("game/${it.id}/30")
                    }, onDelete = { menuViewModel.deletePuzzleSave(it) })
                }
            }
        }
    }
}

@Composable
fun SaveView(puzzleSave: PuzzleSave, onClicked: () -> Unit = {}, onDelete: () -> Unit = {}) {
    val sudoku =
        Sudoku(clues = puzzleSave.clues, entries = puzzleSave.entries, guesses = puzzleSave.guesses)
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clickable() { onClicked() }) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${puzzleSave.id}")
            Text("${puzzleSave.dateWritten}")
            Button(onClick = onDelete) {
                Text("x")
            }
        }
    }
//        SudokuView(sudoku, contradictions = listOf(), controlState = ControlState())
}