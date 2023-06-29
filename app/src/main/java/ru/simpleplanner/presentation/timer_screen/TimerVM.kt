package ru.simpleplanner.presentation.timer_screen

import android.annotation.SuppressLint
import android.os.CountDownTimer
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class TimerVM @Inject constructor() : ViewModel()  {
    val isWorkScreen = mutableStateOf(true)
    val timeTitleScreen = mutableStateOf("0000")
    val isTimerRunning = mutableStateOf(false)
    val isTimerOnPause = mutableStateOf(false)
    val isShortRest = mutableStateOf(true)

    private var timer : CountDownTimer? = null
    val timeLeft = mutableIntStateOf(300000)
    val timeDefault = mutableIntStateOf(300000)

    @SuppressLint("AutoboxingStateValueProperty")
    fun startTimer(millis: Long = timeLeft.value.toLong()) {
        isTimerOnPause.value = false
        timer = object : CountDownTimer(millis, 1000){

            override fun onTick(millisUntilFinished: Long) {
                val minutes = ((millisUntilFinished / 1000) / 60).toInt()
                val seconds = ((millisUntilFinished / 1000) % 60).toInt()
                val timeFormatted: String =
                    java.lang.String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                timeTitleScreen.value = timeFormatted
                timeLeft.value = millisUntilFinished.toInt()
            }

            override fun onFinish() {}
        }.start()
    }

    fun pauseTimer() {
        timer?.cancel()
        isTimerOnPause.value = true
    }

    @SuppressLint("AutoboxingStateValueProperty")
    fun resetTimer() {
        timer?.cancel()
        timeLeft.value = timeDefault.value
        isTimerRunning.value = false

        val minutes = ((timeDefault.value / 1000) / 60)
        val seconds = ((timeDefault.value / 1000) % 60)
        val timeFormatted: String =
            java.lang.String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        timeTitleScreen.value = timeFormatted
    }
}