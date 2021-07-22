package com.maxtyler.sudoku.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.maxtyler.sudoku.database.PuzzleSave
import com.maxtyler.sudoku.model.Difficulty
import com.maxtyler.sudoku.model.Sudoku
import com.maxtyler.sudoku.ui.utils.ControlState
import com.maxtyler.sudoku.ui.utils.stringFormat
import com.maxtyler.sudoku.viewmodels.MenuViewModel
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.launch

@Composable
fun Menu(
    menuViewModel: MenuViewModel = viewModel(),
    navHostController: NavHostController,
    onControlHelp: (() -> Unit) -> Unit = { it() }
) {
    val saves by menuViewModel.saves.collectAsState(initial = listOf())
    val listState = rememberLazyListState()
    val saveCount =
        Difficulty.values().map { it to menuViewModel.puzzleCount(it).collectAsState(initial = 0) }
            .toMap()
//    val saveCount by menuViewModel.puzzleCount.collectAsState(0)
    var deleteAlertState: PuzzleSave? by remember { mutableStateOf(null) }

    val scope = rememberCoroutineScope()
    Scaffold(topBar = { SudokuTopBar(playingGame = false, onControls = onControlHelp) }) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("Start a new game:")
                    Difficulty.values().forEach { difficulty ->
                        val count = saveCount[difficulty]?.value ?: 0
                        Button(
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxWidth(), onClick = {
                                scope.launch {
                                    menuViewModel.createPuzzle(difficulty)?.let {
                                        navHostController.navigate("game/${it}/${difficulty.ordinal}")
                                    }
                                }
                            }, enabled = (count > 0)
                        ) {
                            if (count > 0) Text("Difficulty: ${difficulty.name}")
                            else Text("Waiting for game to generate puzzles...")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text("Continue a previous game:")
                    LazyColumn(state = listState) {

                        items(saves) {
                            Box() {
                                SaveView(it, onClicked = {
                                    navHostController.navigate("game/${it.id}/0")
                                }, onDelete = { deleteAlertState = it })
                            }
                        }
                    }
                }
            }
        }
    }
    if (deleteAlertState != null) {
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            onDismissRequest = { deleteAlertState = null },
            buttons = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.End,
                ) {
                    Button(onClick = {
                        menuViewModel.deletePuzzleSave(deleteAlertState!!)
                        deleteAlertState = null
                    }) {
                        Text("Yes")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(onClick = { deleteAlertState = null }) {
                        Text("No")
                    }
                }
            },
            text = { Text("Really delete this puzzle?") })
    }
}

@Composable
fun SaveView(puzzleSave: PuzzleSave, onClicked: () -> Unit = {}, onDelete: () -> Unit = {}) {
    val sudoku =
        Sudoku(
            clues = puzzleSave.clues.toPersistentMap(),
            entries = puzzleSave.entries.toPersistentMap(),
            guesses = puzzleSave.guesses.toPersistentMap()
        )
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clickable() { onClicked() }) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .aspectRatio(1f)
            ) {
                BoardView(
                    sudoku = sudoku,
                    contradictions = listOf(),
                    controlState = ControlState(null),
                    controlsDisabled = true
                )
            }
            Text("Time: ${puzzleSave.puzzleTime.stringFormat()}")
            Button(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Delete this puzzle")

            }
        }
    }
}