package com.maxtyler.sudoku.ui.utils

import java.time.Duration

fun Duration.stringFormat() = "%d:%02d".format(this.toMinutes(), this.seconds.mod(60))
