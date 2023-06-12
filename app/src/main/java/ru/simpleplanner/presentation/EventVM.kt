package ru.simpleplanner.presentation

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ru.simpleplanner.domain.entities.Calendar
import ru.simpleplanner.domain.use_case.calendar_uc.GetCalendarsUseCase
import java.time.LocalDate
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class EventVM @Inject constructor(
    private val getCalendarsUseCase: GetCalendarsUseCase
): ViewModel() {

    lateinit var calendarList : MutableState<ArrayList<Calendar>>
    lateinit var pickedCalendar : String

    val pickedDate: MutableState<LocalDate> by lazy {
        mutableStateOf(LocalDate.now())
    }

    val permissionsGranted : MutableState<Boolean> by lazy {
        mutableStateOf(false)
    }

    fun getCalendars(){
        calendarList = mutableStateOf(getCalendarsUseCase(permissionsGranted.value))
    }
}