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

@Composable
fun SudokuTopBar(playingGame: Boolean, onClearAllValues: (() -> Unit) -> Unit = {}) {
    var dropDownExpanded = remember { mutableStateOf(false) }
    TopAppBar(title = { Text("Sudoku") }, actions = {
        Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
            IconButton(onClick = { dropDownExpanded.value = true }) {
                Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "hi")
            }
            DropdownMenu(
                expanded = dropDownExpanded.value,
                onDismissRequest = { dropDownExpanded.value = false }) {
                if (playingGame) {
                    DropdownMenuItem(onClick = {
                        onClearAllValues({
                            dropDownExpanded.value = false
                        })
                    })
                    {
                        Text("Clear all values")
                    }
                }
            }
        }
    })
}