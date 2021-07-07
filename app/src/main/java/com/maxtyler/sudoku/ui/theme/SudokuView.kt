package com.maxtyler.sudoku.ui.theme

import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SudokuCell(elements: Set<Int>) {
    Box(modifier = Modifier.size(30.dp), contentAlignment = Alignment.Center) {
        when {
            elements.size == 1 -> Text("${elements.first()}")
            else -> Column(verticalArrangement = Arrangement.SpaceEvenly) {
                (0..2).forEach { row ->
                    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                        (0..2).forEach { col ->
                            val elem = row * 3 + col + 1
                            Text(
                                elem.toString(),
                                color = if (elem in elements) Color.Black else Color.LightGray,
                                fontSize = 3.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SudokuCellPreview() {
    Column() {
        SudokuCell(setOf(1, 2, 3))
        SudokuCell(setOf(9))
    }
}

private fun DrawScope.drawSudokuCell(cell: SudokuDrawState.SudokuState) {
    val paint = Paint()
    paint.textAlign = Paint.Align.CENTER
    paint.isAntiAlias = true
    val bounds: Rect = Rect()

    when (cell) {
        is SudokuDrawState.SudokuState.Clue -> {
            val textSize = size.minDimension / 9
            paint.textSize = textSize
            paint.color = 0xff00ff00.toInt()
            paint.getTextBounds(cell.v.toString(), 0, 1, bounds)

            drawRect(
                color = Color.Black,
                topLeft = Offset(0f, 0f),
                size = Size(textSize, textSize),
                style = Stroke(width = 5f)
            )
            drawIntoCanvas {
                it.nativeCanvas.drawText(
                    cell.v.toString(),
                    textSize / 2,
                    textSize / 2 - bounds.exactCenterY(),
                    paint
                )
            }
        }
        is SudokuDrawState.SudokuState.Guess -> {
            when {
                cell.v.size == 1 -> {
                    val v = cell.v.first()
                    val textSize = size.minDimension / 9
                    paint.textSize = textSize
                    paint.color = 0xffff0000.toInt()
                    paint.getTextBounds(v.toString(), 0, 1, bounds)

                    drawIntoCanvas {
                        it.nativeCanvas.drawText(
                            v.toString(),
                            textSize / 2,
                            textSize / 2 - bounds.exactCenterY(),
                            paint
                        )
                    }
                }
                else -> {
                    val guessCellSize = size.minDimension / 27
                    paint.textSize = guessCellSize

                    (0..2).forEach { row ->
                        (0..2).forEach { col ->
                            val x = row * 3 + col + 1
                            translate(left = col * guessCellSize, top = row * guessCellSize) {
                                paint.getTextBounds(x.toString(), 0, 1, bounds)
                                paint.color =
                                    if (x in cell.v) 0xffff00ff.toInt() else 0x22ff00ff
                                drawIntoCanvas {
                                    it.nativeCanvas.drawText(
                                        x.toString(),
                                        guessCellSize / 2,
                                        guessCellSize / 2 - bounds.exactCenterY(),
                                        paint
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun SudokuView(sudokuDrawState: SudokuDrawState) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val boardSize = size.minDimension
        val lineSpacing = boardSize / 9
        (0..9).forEach {
            val pos = it * lineSpacing
            drawLine(
                color = Color.Black,
                start = Offset(0f, pos),
                end = Offset(boardSize, pos),
                strokeWidth = if (it.mod(3) == 0) 5f else 1f
            )
            drawLine(
                color = Color.Black,
                start = Offset(pos, 0f),
                end = Offset(pos, boardSize),
                strokeWidth = if (it.mod(3) == 0) 5f else 1f
            )
        }

        sudokuDrawState.values.forEach { (row, col), v ->
            translate(left = col * lineSpacing, top = row * lineSpacing) {
                drawSudokuCell(v)
            }
        }
    }
//    Column() {
//        (0..8).forEach { row ->
//            Row() {
//                (0..8).forEach { col ->
//                    SudokuCell(sudoku.board.getOrDefault(Pair(row, col), (1..9).toSet()))
//                }
//            }
//        }
//    }
}