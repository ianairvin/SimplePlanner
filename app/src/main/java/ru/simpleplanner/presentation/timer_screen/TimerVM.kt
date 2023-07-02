package ru.simpleplanner.presentation.timer_screen

import android.annotation.SuppressLint
import android.app.Application
import android.os.CountDownTimer
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.simpleplanner.domain.use_case.timer_uc.GetTimeUseCase
import ru.simpleplanner.domain.use_case.timer_uc.UpdateTimeUseCase
import ru.simpleplanner.presentation.task_screen.TaskVM
import javax.inject.Inject


@SuppressLint("AutoboxingStateValueProperty")
@HiltViewModel
class TimerVM @Inject constructor(
    private val getTimeUseCase: GetTimeUseCase,
    private val updateTimeUseCase: UpdateTimeUseCase,
    appContext: Application
) : ViewModel() {

    private var timer: CountDownTimer? = null

    val timeDefaultWork = mutableLongStateOf(0L)
    val timeDefaultShortRest = mutableLongStateOf(0L)
    val timeDefaultLongRest = mutableLongStateOf(0L)

    var currentTimeMode = timeDefaultWork
    val timeLeft = mutableLongStateOf(0L)

    val currentScreen = mutableIntStateOf(1)

    val numberOfRepeats = mutableIntStateOf(0)

    val isTimerRunning = mutableStateOf(false)
    val isTimerOnPause = mutableStateOf(false)
    val timeTitleScreen = mutableStateOf("")

    val service = NotificationService(appContext)

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
                isTimerRunning.value = false
                isTimerOnPause.value = false
                numberOfRepeats.value++
                when (currentTimeMode) {
                    timeDefaultWork -> if (numberOfRepeats.value >= 4) {
                                            currentTimeMode = timeDefaultLongRest
                                            currentScreen.value = 3
                                        } else {
                                            currentTimeMode = timeDefaultShortRest
                                            currentScreen.value = 2
                                        }

                    timeDefaultShortRest -> { currentTimeMode = timeDefaultWork
                    currentScreen.value = 1 }

                    timeDefaultLongRest -> { currentTimeMode = timeDefaultWork
                        numberOfRepeats.value = 0
                        currentScreen.value = 1 }
                }
                timeLeft.value = currentTimeMode.value
                minutes = ((timeLeft.value / 1000) / 60)
                timeTitleScreen.value = String.format("%02d:00", minutes)
                service.show(currentScreen.value == 1)
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
        timeTitleScreen.value = String.format("%02d:00", minutes)
    }

    fun saveTime() = viewModelScope.launch {
        updateTimeUseCase(
            timeDefaultWork.value,
            timeDefaultShortRest.value,
            timeDefaultLongRest.value,
            numberOfRepeats.value
        )
    }

    init{
        viewModelScope.launch {
            timeDefaultWork.value = getTimeUseCase.getTimeWork()
            timeDefaultShortRest.value = getTimeUseCase.getTimeShortBreak()
            timeDefaultLongRest.value = getTimeUseCase.getTimeLongBreak()
            numberOfRepeats.value = getTimeUseCase.getNumberOfRepeats()
            val minutes = ((timeDefaultWork.value / 1000) / 60)
            val seconds = ((timeDefaultWork.value / 1000) % 60)
            timeTitleScreen.value = String.format("%02d:%02d", minutes, seconds)
        }
    }
}