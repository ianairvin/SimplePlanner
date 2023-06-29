package ru.simpleplanner.presentation.timer_screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TimerVM @Inject constructor(

): ViewModel() {
    val isWorkScreen = mutableStateOf(true)
    val time = mutableStateOf("0000")
    val start = mutableStateOf(false)
    val isShortRest = mutableStateOf(true)
}