package com.maxtyler.sudoku.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

sealed class SelectionType {
    data class Entry(val x: Int) : SelectionType()
    data class Guess(val x: Set<Int>) : SelectionType()
}

@ExperimentalFoundationApi
@Composable
fun NumberGrid(
    selected: Set<Int>,
    onItemPressed: (Int) -> Unit = {},
    onLongItemPressed: (Int) -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        (0..2).forEach { col ->
            Column() {
                (0..2).forEach { row ->
                    val x = row * 3 + col + 1
                    LongPressButton(
                        modifier = Modifier.padding(1.dp),
                        onClick = { onItemPressed(x) },
                        onLongClick = { onLongItemPressed(x) },
                        colors = ButtonDefaults.buttonColors(backgroundColor = if (x in selected) Color.Gray else Color.LightGray)
                    ) {
                        Text(x.toString())
                    }
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun LongPressButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    interactionState: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = ButtonDefaults.elevation(),
    shape: Shape = MaterialTheme.shapes.small,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val contentColor by colors.contentColor(enabled)
    Surface(
        shape = shape,
        color = colors.backgroundColor(enabled).value,
        contentColor = contentColor.copy(alpha = 1f),
        border = border,
        elevation = elevation?.elevation(enabled, interactionState)?.value ?: 0.dp,
        modifier = modifier.combinedClickable(
            interactionSource = interactionState,
            indication = null,
            onClick = onClick,
            onDoubleClick = onDoubleClick,
            onLongClick = onLongClick,
            enabled = enabled,
            role = Role.Button
        )
    ) {
        CompositionLocalProvider(LocalContentAlpha provides contentColor.alpha) {
            ProvideTextStyle(
                value = MaterialTheme.typography.button
            ) {
                Row(
                    Modifier
                        .defaultMinSize(
                            minWidth = ButtonDefaults.MinWidth,
                            minHeight = ButtonDefaults.MinHeight
                        )
                        .indication(interactionState, rememberRipple())
                        .padding(contentPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
fun SelectionView(
    entry: Int?,
    guess: Set<Int>,
    onEntryPressed: (Int) -> Unit = {},
    onGuessPressed: (Int) -> Unit = {},
    onUndoPressed: () -> Unit = {},
    onRedoPressed: () -> Unit = {},
    onCleanGuessesPressed: () -> Unit = {},
    onControlHelpPressed: () -> Unit = {},
    undoEnabled: Boolean = true,
    redoEnabled: Boolean = true,
) {
    Row() {
        Button(onClick = onUndoPressed, enabled = undoEnabled) {
            Text("UNDO")
        }
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Input")
                NumberGrid(entry?.let { setOf(it) } ?: guess,
                    onItemPressed = onGuessPressed,
                    onLongItemPressed = onEntryPressed)
            }
            Spacer(modifier = Modifier.height(14.dp))
            Button(onClick = onCleanGuessesPressed) {
                Text("CLEAN GUESSES")
            }
            Spacer(modifier = Modifier.height(14.dp))
            Button(onClick = onControlHelpPressed) {
                Text("CONTROLS")
            }
        }
        Button(onClick = onRedoPressed, enabled = redoEnabled) {
            Text("REDO")
        }
    }
}