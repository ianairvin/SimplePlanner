package ru.simpleplanner.presentation.timer_screen

import android.annotation.SuppressLint
import android.app.Application
import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.simpleplanner.domain.use_case.timer_uc.GetTimeUseCase
import ru.simpleplanner.domain.use_case.timer_uc.UpdateTimeUseCase
import javax.inject.Inject


@SuppressLint("AutoboxingStateValueProperty")
@HiltViewModel
class TimerVM @Inject constructor(
    private val getTimeUseCase: GetTimeUseCase,
    private val updateTimeUseCase: UpdateTimeUseCase,
    private val appContext: Application
) : ViewModel() {

    private var timer: CountDownTimer? = null

    val timeDefaultWork = mutableLongStateOf(0L)
    val timeDefaultShortRest = mutableLongStateOf(0L)
    val timeDefaultLongRest = mutableLongStateOf(0L)

    var currentTimeMode = timeDefaultWork
    val timeLeft = mutableLongStateOf(0L)

    val isWorkScreen = mutableStateOf(true)
    val isTimerRunning = mutableStateOf(false)
    val isTimerOnPause = mutableStateOf(false)
    val isShortRest = mutableStateOf(true)
    val timeTitleScreen = mutableStateOf("")

    val service = NotificationService(appContext)
    init{
        viewModelScope.launch {
            timeDefaultWork.value = getTimeUseCase.getTimeWork()
            timeDefaultShortRest.value = getTimeUseCase.getTimeShortBreak()
            timeDefaultLongRest.value = getTimeUseCase.getTimeLongBreak()
            val minutes = ((timeDefaultWork.value / 1000) / 60)
            val seconds = ((timeDefaultWork.value / 1000) % 60)
            timeTitleScreen.value = String.format("%02d:%02d", minutes, seconds)
        }
    }

    @SuppressLint("AutoboxingStateValueProperty")
    fun startTimer(millis: Long = timeLeft.value) {
        isTimerOnPause.value = false
        timer = object : CountDownTimer(millis, 1000){
        var minutes = 0L
        var seconds = 0L

            override fun onTick(millisUntilFinished: Long) {
                minutes = ((millisUntilFinished / 1000) / 60)
                seconds = ((millisUntilFinished / 1000) % 60)

                timeTitleScreen.value = String.format("%02d:%02d", minutes, seconds)
                timeLeft.value = millisUntilFinished
            }

            override fun onFinish() {
                timeLeft.value = currentTimeMode.value
                isTimerRunning.value = false
                isTimerOnPause.value = false
                val minutes = ((timeLeft.value / 1000) / 60)
                timeTitleScreen.value = "$minutes:00"

                service.show(isWorkScreen.value)
            }
        }.start()
    }

    fun pauseTimer() {
        timer?.cancel()
        isTimerOnPause.value = true
    }


    @SuppressLint("AutoboxingStateValueProperty")
    fun resetTimer() {
        timer?.cancel()
        timeLeft.value = currentTimeMode.value
        isTimerRunning.value = false
        isTimerOnPause.value = false
        val minutes = ((timeLeft.value / 1000) / 60)
        timeTitleScreen.value = "$minutes:00"
    }

    fun saveTime() = viewModelScope.launch {
        Log.i("qqqq", timeDefaultWork.value.toString())
        updateTimeUseCase(timeDefaultWork.value, timeDefaultShortRest.value, timeDefaultLongRest.value)
    }

}