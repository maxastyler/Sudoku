package com.maxtyler.sudoku.ui

import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.toSize
import kotlin.math.floor
import kotlin.math.roundToInt

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
fun BoardView(
    sudokuDrawState: SudokuDrawState,
    controlState: ControlState,
    onCellPressed: (Pair<Int, Int>) -> Unit = {}
) {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .pointerInput(Unit) {
            detectTapGestures(onPress = { (x, y) ->
                val squareSize = size.toSize().minDimension / 9
                onCellPressed(
                    Pair(
                        floor(x / squareSize).roundToInt(), floor(y / squareSize).roundToInt()
                    )
                )
            })
        }) {
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
            translate(left = row * lineSpacing, top = col * lineSpacing) {
                drawSudokuCell(v)
            }
        }
        controlState.selected?.let { (x, y) ->
            drawRect(
                color = Color.Cyan,
                topLeft = Offset(x * lineSpacing, y * lineSpacing),
                size = Size(lineSpacing, lineSpacing),
                style = Stroke(width = 10f)
            )
        }
    }
}
