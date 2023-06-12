package ru.simpleplanner.presentation

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

@HiltViewModel
class EventVM @Inject constructor(
    private val getCalendarsUseCase: GetCalendarsUseCase,
    private val getEventsUseCase: GetEventsUseCase
): ViewModel() {

    lateinit var calendars : MutableState<ArrayList<Calendar>>
    lateinit var events : MutableState<ArrayList<Event>>

    val selectedDate: MutableState<LocalDate> by lazy {
        mutableStateOf(LocalDate.now())
    }

    val selectedCalendar: MutableState<String> by lazy {
        mutableStateOf("")
    }

    val permissionsGranted : MutableState<Boolean> by lazy {
        mutableStateOf(false)
    }

    fun getCalendars(){
        calendars = mutableStateOf(getCalendarsUseCase(permissionsGranted.value))
        selectedCalendar.value = calendars.value[0].displayName
        getEvents()
    }

    fun getEvents(){
        events = mutableStateOf(getEventsUseCase(selectedDate.value))
    }
}