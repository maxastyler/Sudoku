package com.maxtyler.sudoku.model

data class Sudoku(val board: Map<Pair<Int, Int>, Set<Int>>) {
    companion object {

    }
}
