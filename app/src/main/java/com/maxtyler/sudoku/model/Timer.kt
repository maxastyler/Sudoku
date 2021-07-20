package com.maxtyler.sudoku.model

import android.util.Log
import com.maxtyler.sudoku.ui.utils.stringFormat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import java.time.Duration
import java.time.LocalDateTime

/**
 * A timer for the game. Subscribe to the duration flow to get time updates every second
 * Can be paused and restarted
 * @param parentScope The scope to run the timer updates in
 * @param completionFlow A flow telling whether the puzzle is completed or not
 * @param totalTime Initialise the timer with the given duration
 */
class Timer(
    private val parentScope: CoroutineScope,
    private val completionFlow: StateFlow<Boolean>,
    private var totalTime: Duration = Duration.ZERO
) {

    private var timerJob: Job? = null
    private var startTime: LocalDateTime? = null
    private val _duration: MutableStateFlow<Duration> = MutableStateFlow(totalTime)
    val duration = _duration.asStateFlow()
    val time: Duration
        get() = totalTime + (startTime?.let {
            Duration.between(
                it,
                LocalDateTime.now()
            )
        } ?: Duration.ZERO)

    init {
        parentScope.launch {
            completionFlow.collect { if (it) stopTimer() else startTimer() }
        }
    }

    /**
     * Set the total time to the new time and set the start time to now
     * If the timer is running (startTime != null), then reset the start time to the current value
     * @param newTime The new time to set the timer to
     */
    fun setTime(newTime: Duration) {
        totalTime = newTime
        if (startTime != null) startTime = LocalDateTime.now()
        _duration.value = time
    }

    /**
     * If the timer's start time hasn't been set, set it now.
     * Start the duration emission job
     */
    fun startTimer() {
        if (!completionFlow.value) {
            if (startTime == null) startTime = LocalDateTime.now()
            _duration.value = time
            timerJob?.cancel()
            timerJob = parentScope.launch {
                while (true) {
                    _duration.emit(time)
                    delay(1000)
                }
            }
        }
    }

    /**
     * Stop the timer, and add the duration to the total time
     */
    fun stopTimer() {
        timerJob?.cancel()
        if (startTime != null) {
            totalTime = time
            startTime = null
            _duration.value = time
        }
    }
}