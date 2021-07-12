package com.maxtyler.sudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maxtyler.sudoku.ui.Main
import com.maxtyler.sudoku.ui.SudokuViewModel
import com.maxtyler.sudoku.ui.theme.SudokuTheme
import com.maxtyler.sudoku.ui.theme.SudokuView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SudokuTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Main()
                }
            }
        }
    }
}

@Composable
fun RunFunctionOnPauseAndResume(onPause: () -> Unit, onResume: () -> Unit) {
    val pause by rememberUpdatedState(newValue = onPause)
    val resume by rememberUpdatedState(newValue = onResume)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val observer = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> pause()
            Lifecycle.Event.ON_RESUME -> resume()
            else -> Unit
        }
    }
    DisposableEffect(key1 = lifecycle) {
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
}