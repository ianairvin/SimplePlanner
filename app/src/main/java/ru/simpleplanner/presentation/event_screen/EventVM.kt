package ru.simpleplanner.presentation.event_screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.simpleplanner.domain.use_case.calendar_uc.GetCalendarsUseCase
import java.time.LocalDate
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.simpleplanner.domain.entities.Event
import ru.simpleplanner.domain.entities.Calendar
import ru.simpleplanner.domain.entities.Task
import ru.simpleplanner.domain.use_case.calendar_uc.GetPickedCalendarsUseCase
import ru.simpleplanner.domain.use_case.calendar_uc.InsertPickedCalendarsUseCase
import ru.simpleplanner.domain.use_case.event_uc.DeleteEventUseCase
import ru.simpleplanner.domain.use_case.event_uc.GetEventsUseCase
import ru.simpleplanner.domain.use_case.event_uc.GetOneEventUseCase
import ru.simpleplanner.domain.use_case.event_uc.InsertEventUseCase
import ru.simpleplanner.domain.use_case.event_uc.UpdateEventUseCase
import ru.simpleplanner.domain.use_case.task_uc.EditStatusTaskUseCase
import ru.simpleplanner.domain.use_case.task_uc.GetTasksForSpecificDay
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.TimeZone

@SuppressLint("MutableCollectionMutableState")
@HiltViewModel
class EventVM @Inject constructor(
    private val deleteEventUseCase: DeleteEventUseCase,
    private val insertPickedCalendarsUseCase: InsertPickedCalendarsUseCase,
    private val getCalendarsUseCase: GetCalendarsUseCase,
    private val getPickedCalendarsUseCase: GetPickedCalendarsUseCase,
    private val getEventsUseCase: GetEventsUseCase,
    private val getOneEventUseCase: GetOneEventUseCase,
    private val insertEventUseCase: InsertEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val getTasksForSpecificDayUseCase: GetTasksForSpecificDay,
    private val editStatusTaskUseCase: EditStatusTaskUseCase
): ViewModel() {

    val selectedDate: MutableState<LocalDate> by lazy {
        mutableStateOf(LocalDate.now())
    }

    val selectedCalendarsId : MutableState<MutableList<String>> by lazy {
        mutableStateOf(mutableListOf())
    }

    val repeatRule = arrayOf(
        arrayOf("Нет", ""),
        arrayOf("Каждый день", "DAILY/1"),
        arrayOf("Каждые два дня", "DAILY/2"),
        arrayOf("Каждую неделю", "WEEKLY/1"),
        arrayOf("Каждые две недели", "WEEKLY/2"),
        arrayOf("Каждый месяц", "MONTHLY/1")
    )

    var calendarsList = mutableStateOf(emptyList<Calendar>())
    var eventsList = mutableStateOf(emptyList<Event>())
    var tasksList = mutableStateOf(emptyList<Task>())


    val calendarIdForBottomSheet =  mutableStateOf("")
    val calendarDisplayNameForBottomSheet = mutableStateOf("")
    val titleForBottomSheet =  mutableStateOf("")
    val locationForBottomSheet =  mutableStateOf("")
    val startForBottomSheet = mutableStateOf(LocalTime.now().plusHours(1).withMinute(0))
    val endForBottomSheet = mutableStateOf(startForBottomSheet.value.plusHours(1))
    val allDayForBottomSheet = mutableIntStateOf(0)
    val repeatRuleForBottomSheet =  mutableStateOf(arrayOf("Нет", ""))
    val descriptionForBottomSheet = mutableStateOf("")
    val pickedDateForBottomSheet =  mutableStateOf(LocalDate.now())
    private val idEventForBottomSheet =  mutableStateOf("")

    val updaterBottomSheet = mutableStateOf(false)

    private fun getCalendars() = viewModelScope.launch{
        calendarsList.value = getCalendarsUseCase()
    }

    private fun getPickedCalendars() = viewModelScope.launch{
        selectedCalendarsId.value = getPickedCalendarsUseCase() as MutableList<String>
    }

    fun getEvents() = viewModelScope.launch{
        eventsList.value = getEventsUseCase(selectedDate.value, selectedCalendarsId.value)
    }

    fun getTasks() = viewModelScope.launch{
        tasksList.value = getTasksForSpecificDayUseCase(selectedDate.value)
    }

    @SuppressLint("AutoboxingStateValueProperty")
    fun newEventForBottomSheet(){
        calendarIdForBottomSheet.value =
            if(calendarsList.value.isNotEmpty()){
                calendarsList.value[0].id
            } else { "" }
        calendarDisplayNameForBottomSheet.value =
            if(calendarsList.value.isNotEmpty()){
                calendarsList.value[0].displayName
            } else { "" }
        titleForBottomSheet.value = ""
        locationForBottomSheet.value =  ""
        startForBottomSheet.value = LocalTime.now().plusHours(1).withMinute(0)
        endForBottomSheet.value = startForBottomSheet.value.plusHours(1)
        allDayForBottomSheet.value = 0
        repeatRuleForBottomSheet.value = arrayOf("Нет", "")
        descriptionForBottomSheet.value = ""
        pickedDateForBottomSheet.value = selectedDate.value
        updaterBottomSheet.value = false
    }

    @SuppressLint("AutoboxingStateValueProperty")
    fun pickedEventForBottomSheet(id: String, calendarId: String, start: LocalDateTime, end: LocalDateTime) {
        viewModelScope.launch {
            val event = getOneEventUseCase(id, calendarId, start, end)
            calendarIdForBottomSheet.value = event.calendarId
            calendarDisplayNameForBottomSheet.value = event.calendarDisplayName
            titleForBottomSheet.value = event.title
            locationForBottomSheet.value = event.location ?: ""
            startForBottomSheet.value = event.start.toLocalTime()
            endForBottomSheet.value = event.end.toLocalTime()
            allDayForBottomSheet.value = event.allDay
            repeatRuleForBottomSheet.value = arrayOf("Нет", "")
            repeatRule.forEach { item ->
                if (item[1] == event.repeatRule) {
                    repeatRuleForBottomSheet.value = arrayOf(item[0], item[1])
                }
            }
            descriptionForBottomSheet.value = event.description ?: ""
            pickedDateForBottomSheet.value = event.start.toLocalDate()
            idEventForBottomSheet.value = event.id
            updaterBottomSheet.value = true
        }
    }

    @SuppressLint("AutoboxingStateValueProperty")
    fun saveOrUpdateEvent() {
        val startDateTime =
            pickedDateForBottomSheet.value.atStartOfDay()
                .plusSeconds(startForBottomSheet.value.toSecondOfDay().toLong())
        val endDateTime =
            pickedDateForBottomSheet.value.atStartOfDay()
                .plusSeconds(endForBottomSheet.value.toSecondOfDay().toLong())

        val event = Event(
            calendarIdForBottomSheet.value,
            calendarDisplayNameForBottomSheet.value,
            titleForBottomSheet.value,
            locationForBottomSheet.value,
            startDateTime,
            endDateTime,
            allDayForBottomSheet.value,
            repeatRuleForBottomSheet.value[1],
            descriptionForBottomSheet.value,
            TimeZone.getDefault().toString(),
            idEventForBottomSheet.value,
            null,
            null
        )
        if(updaterBottomSheet.value){
            updateEvent(event)
        } else {
            insertEvent(event)
        }
       // getEvents()
    }

    private fun updateEvent(event: Event) = viewModelScope.launch{
        updateEventUseCase(event)
    }

    private fun insertEvent(event: Event) = viewModelScope.launch{
        insertEventUseCase(event)
    }

    fun deleteEvent() =
        viewModelScope.launch {
            deleteEventUseCase(idEventForBottomSheet.value)
            // getEvents()
        }

    fun savePickedCalendars() = viewModelScope.launch{
        insertPickedCalendarsUseCase(selectedCalendarsId.value)
    }

    fun editStatus(id: Int, check: Boolean) =
        viewModelScope.launch {
            editStatusTaskUseCase(id, check)
        }

    init{
        getCalendars()
        getPickedCalendars()
    }
}