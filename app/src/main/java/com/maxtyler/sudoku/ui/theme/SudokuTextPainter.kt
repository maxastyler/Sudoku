package com.maxtyler.sudoku.ui.theme

import android.graphics.Paint


object SudokuTextPainter {
    val guessPaint = Paint()
    val cluePaint = Paint()
    init {
        guessPaint.isAntiAlias = true
        guessPaint.textAlign = Paint.Align.CENTER
        guessPaint.color = 0xff00ff00.toInt()
    }
}