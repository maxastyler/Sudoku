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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.toSize
import com.maxtyler.sudoku.model.Sudoku
import kotlin.math.floor
import kotlin.math.roundToInt

private fun DrawScope.drawClue(
    clue: Int,
    paint: Paint,
    textColor: Color = Color(0xff000000),
    backgroundColor: Color = Color(0xffeeeeee),
    boxColor: Color = Color.Black,
    boxThickness: Float = 10f
) {
    val bounds = Rect()
    val textSize = size.minDimension / 9
    paint.textSize = textSize
    paint.color = textColor.toArgb()
    paint.getTextBounds(clue.toString(), 0, 1, bounds)

    drawRect(
        color = backgroundColor,
        topLeft = Offset(0f, 0f),
        size = Size(textSize, textSize),
    )

    drawRect(
        color = boxColor,
        topLeft = Offset(0f, 0f),
        size = Size(textSize, textSize),
        style = Stroke(width = boxThickness)
    )
    drawIntoCanvas {
        it.nativeCanvas.drawText(
            clue.toString(),
            textSize / 2,
            textSize / 2 - bounds.exactCenterY(),
            paint
        )
    }
}

private fun DrawScope.drawEntry(entry: Int, paint: Paint, textColor: Color = Color(0xff000000)) {
    val bounds = Rect()
    val textSize = size.minDimension / 9
    paint.textSize = textSize
    paint.color = textColor.toArgb()
    paint.getTextBounds(entry.toString(), 0, 1, bounds)

    drawIntoCanvas {
        it.nativeCanvas.drawText(
            entry.toString(),
            textSize / 2,
            textSize / 2 - bounds.exactCenterY(),
            paint
        )
    }
}

private fun DrawScope.drawGuess(
    guess: Set<Int>,
    paint: Paint,
    guessColor: Color = Color(0xff666666),
    noGuessColor: Color = Color(0x00ffffff)
) {
    val guessCellSize = size.minDimension / 27
    paint.textSize = guessCellSize
    val bounds = Rect()

    (0..2).forEach { row ->
        (0..2).forEach { col ->
            val x = row * 3 + col + 1
            translate(left = col * guessCellSize, top = row * guessCellSize) {
                paint.getTextBounds(x.toString(), 0, 1, bounds)
                paint.color =
                    if (x in guess) guessColor.toArgb() else noGuessColor.toArgb()
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

fun DrawScope.drawGrid(color: Color = Color.Black, thinWidth: Float = 1f, thickWidth: Float = 10f) {
    val boardSize = size.minDimension
    val lineSpacing = boardSize / 9
    (0..9).forEach {
        val pos = it * lineSpacing
        drawLine(
            color = Color.Black,
            start = Offset(0f, pos),
            end = Offset(boardSize, pos),
            strokeWidth = if (it.mod(3) == 0) thickWidth else thinWidth
        )
        drawLine(
            color = Color.Black,
            start = Offset(pos, 0f),
            end = Offset(pos, boardSize),
            strokeWidth = if (it.mod(3) == 0) thickWidth else thinWidth
        )
    }
}

fun DrawScope.drawNumbers(sudoku: Sudoku, contradictions: List<Pair<Int, Int>>) {
    val paint = Paint()
    paint.textAlign = Paint.Align.CENTER
    paint.isAntiAlias = true

    val boardSize = size.minDimension
    val lineSpacing = boardSize / 9

    (0..8).forEach { row ->
        (0..8).forEach { col ->
            val coord = row to col
            translate(left = row * lineSpacing, top = col * lineSpacing) {
                sudoku.clues[coord]?.let { clue ->
                    drawClue(clue, paint)
                } ?: sudoku.entries[coord]?.let { entry ->
                    drawEntry(
                        entry,
                        paint,
                        if (coord in contradictions) Color(0xffff0000) else Color(0xff000000)
                    )
                } ?: sudoku.guesses.getOrDefault(coord, setOf()).let { guess ->
                    drawGuess(guess, paint)
                }
            }
        }
    }
}

fun DrawScope.drawSelection(
    controlState: ControlState,
    color: Color = Color.LightGray,
    strokeWidth: Float = 10f
) {
    val boardSize = size.minDimension
    val lineSpacing = boardSize / 9

    controlState.selected?.let { (x, y) ->
        drawRect(
            color = color,
            topLeft = Offset(x * lineSpacing, y * lineSpacing),
            size = Size(lineSpacing, lineSpacing),
            style = Stroke(
                width = strokeWidth,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f))
            )
        )
    }

}

@Composable
fun BoardView(
    sudoku: Sudoku,
    contradictions: List<Pair<Int, Int>>,
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

        drawGrid()

        drawNumbers(sudoku, contradictions)

        drawSelection(controlState)
    }
}
