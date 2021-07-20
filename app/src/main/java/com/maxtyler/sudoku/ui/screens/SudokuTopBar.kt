package com.maxtyler.sudoku.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.maxtyler.sudoku.ui.utils.stringFormat
import java.time.Duration

@Composable
fun SudokuTopBar(
    playingGame: Boolean,
    time: Duration? = null,
    onClearAllValues: (() -> Unit) -> Unit = { it() },
    onClearGuesses: (() -> Unit) -> Unit = { it() },
    onClearEntries: (() -> Unit) -> Unit = { it() },
    onControls: (() -> Unit) -> Unit = { it() },
) {
    var dropDownExpanded = remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text(time?.let { "Sudoku | Time: ${it.stringFormat()}" } ?: "Sudoku") },
        actions = {
            Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                IconButton(onClick = { dropDownExpanded.value = true }) {
                    Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "hi")
                }
                DropdownMenu(
                    expanded = dropDownExpanded.value,
                    onDismissRequest = { dropDownExpanded.value = false }) {
                    DropdownMenuItem(onClick = {
                        onControls({ dropDownExpanded.value = false })
                    }) {
                        Text("Controls help")
                    }
                    if (playingGame) {
                        Divider()
                        DropdownMenuItem(onClick = {
                            onClearGuesses({ dropDownExpanded.value = false })
                        }) {
                            Text("Clear all guesses")
                        }
                        DropdownMenuItem(onClick = {
                            onClearEntries({ dropDownExpanded.value = false })
                        }) {
                            Text("Clear all entries")
                        }
                        DropdownMenuItem(onClick = {
                            onClearAllValues({
                                dropDownExpanded.value = false
                            })
                        })
                        {
                            Text("Clear everything")
                        }
                    }
                }
            }
        })
}