package ru.simpleplanner.presentation

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import ru.simpleplanner.domain.entities.Calendar
import ru.simpleplanner.domain.use_case.calendar_uc.GetCalendarsUseCase
import java.time.LocalDate
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.simpleplanner.domain.entities.Event
import ru.simpleplanner.domain.use_case.event_uc.GetEventsUseCase
import java.time.ZoneOffset

@HiltViewModel
class EventVM @Inject constructor(
    private val getCalendarsUseCase: GetCalendarsUseCase,
    private val getEventsUseCase: GetEventsUseCase
): ViewModel() {

    lateinit var calendars : MutableState<List<Calendar>>
    lateinit var events : MutableState<List<Event>>

    val selectedDate: MutableState<LocalDate> by lazy {
        mutableStateOf(LocalDate.now())
    }

    val permissionsGranted : MutableState<Boolean> by lazy {
        mutableStateOf(false)
    }

    val selectedCalendarName: MutableState<String> by lazy {
        mutableStateOf("")
    }

    val selectedCalendarId: MutableState<String> by lazy {
        mutableStateOf("")
    }

    fun getCalendars(){
        calendars = mutableStateOf(getCalendarsUseCase(permissionsGranted.value))
        Log.i("changeCalendar", selectedCalendarName.value)
    }

    fun getEvents(){
        events = mutableStateOf(getEventsUseCase(selectedDate.value, selectedCalendarId.value))
    }
}