package ru.simpleplanner.presentation

import android.Manifest
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import ru.simpleplanner.domain.entities.Calendar
import ru.simpleplanner.domain.use_case.calendar_uc.GetCalendarsUseCase
import java.time.LocalDate
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.simpleplanner.domain.entities.Event
import ru.simpleplanner.domain.use_case.event_uc.GetEventsUseCase
import ru.simpleplanner.domain.use_case.event_uc.InsertEventUseCase

@HiltViewModel
class EventVM @Inject constructor(
    private val getCalendarsUseCase: GetCalendarsUseCase,
    private val getEventsUseCase: GetEventsUseCase,
    private val insertEventUseCase: InsertEventUseCase
): ViewModel() {

    var calendars = mutableStateOf(emptyList<Calendar>())
    var events = mutableStateOf(emptyList<Event>())

    val selectedDate: MutableState<LocalDate> by lazy {
        mutableStateOf(LocalDate.now())
    }

    val permissionsGranted : MutableState<Boolean> by lazy {
        mutableStateOf(false)
    }

    val selectedCalendarsId : MutableState<ArrayList<String>> by lazy {
        mutableStateOf(ArrayList())
    }

    val repeatRule = arrayOf(
        arrayOf("Нет", ""),
        arrayOf("Каждый день", "DAILY"),
        arrayOf("Каждые два дня", "DAYLY/2"),
        arrayOf("Каждую неделю", "WEEKLY"),
        arrayOf("Каждые две недели", "WEEKLY/2"),
        arrayOf("Каждый месяц", "MONTHLY")
    )

    fun getCalendars(){
        calendars = mutableStateOf(getCalendarsUseCase(permissionsGranted.value))
    }

    fun getEvents(){
        events = mutableStateOf(getEventsUseCase(selectedDate.value, selectedCalendarsId.value))
    }

    fun saveEvent(event: Event){
        insertEventUseCase(event)
        getEvents()
    }
}