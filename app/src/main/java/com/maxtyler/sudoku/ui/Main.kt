package com.maxtyler.sudoku.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController

@Composable
fun Main() {
    val navController = rememberNavController()
    NavHost(navController, "menu") {
        composable("menu") {
            val vm = hiltViewModel<MenuViewModel>()
            Menu(vm, navController)
        }
        composable(
            "game/{gameId}/{numberOfClues}",
            arguments = listOf(navArgument("gameId") { type = NavType.LongType },
                navArgument("numberOfClues") { type = NavType.IntType })
        ) {
            val vm = hiltViewModel<SudokuViewModel>()
            SudokuScreen(vm)
        }
    }
}