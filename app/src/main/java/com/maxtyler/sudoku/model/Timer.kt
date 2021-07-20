package com.maxtyler.sudoku.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

/**
 * A timer for the game. Subscribe to the duration flow to get time updates every second
 * Can be paused and restarted
 * @param parentScope The scope to run the timer updates in
 * @param totalTime Initialise the timer with the given duration
 */
class Timer(
    private val parentScope: CoroutineScope,
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
        startTimer()
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

    /**
     * Stop the timer, and add the duration to the total time
     */
    fun stopTimer() {
        timerJob?.cancel()
        startTime?.let {
            totalTime += Duration.between(it, LocalDateTime.now())
        }
        _duration.value = time
        startTime = null
    }
}