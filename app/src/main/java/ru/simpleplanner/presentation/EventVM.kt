package ru.simpleplanner.presentation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import ru.simpleplanner.domain.use_case.calendar_uc.GetCalendarsUseCase
import java.time.LocalDate
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.simpleplanner.domain.entities.Event
import ru.simpleplanner.domain.use_case.event_uc.DeleteEventUseCase
import ru.simpleplanner.domain.use_case.event_uc.GetEventsUseCase
import ru.simpleplanner.domain.use_case.event_uc.GetOneEventUseCase
import ru.simpleplanner.domain.use_case.event_uc.InsertEventUseCase
import ru.simpleplanner.domain.use_case.event_uc.UpdateEventUseCase
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.TimeZone

@HiltViewModel
class EventVM @Inject constructor(
    private val deleteEventUseCase: DeleteEventUseCase,
    private val getCalendarsUseCase: GetCalendarsUseCase,
    private val getEventsUseCase: GetEventsUseCase,
    private val getOneEventUseCase: GetOneEventUseCase,
    private val insertEventUseCase: InsertEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase
): ViewModel() {

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
        arrayOf("Каждый день", "DAILY/1"),
        arrayOf("Каждые два дня", "DAILY/2"),
        arrayOf("Каждую неделю", "WEEKLY/1"),
        arrayOf("Каждые две недели", "WEEKLY/2"),
        arrayOf("Каждый месяц", "MONTHLY/1")
    )


    var calendarsList = mutableStateOf(getCalendarsUseCase(permissionsGranted.value))
    var eventsList = mutableStateOf(
        getEventsUseCase(selectedDate.value, selectedCalendarsId.value))

    val calendarIdForBottomSheet =  mutableStateOf("")
    val calendarDisplayNameForBottomSheet = mutableStateOf("")
    val titleForBottomSheet =  mutableStateOf("")
    val locationForBottomSheet =  mutableStateOf("")
    val startForBottomSheet = mutableStateOf(LocalTime.now())
    val endForBottomSheet = mutableStateOf(LocalTime.now().plusHours(1))
    val allDayForBottomSheet = mutableIntStateOf(0)
    val repeatRuleForBottomSheet =  mutableStateOf(arrayOf("Нет", ""))
    val descriptionForBottomSheet = mutableStateOf("")
    val pickedDateForBottomSheet =  mutableStateOf(LocalDate.now())
    val idEventForBottomSheet =  mutableStateOf("")

    val updaterBottomSheet = mutableStateOf(false)

    fun getCalendars(){
        calendarsList.value = getCalendarsUseCase(permissionsGranted.value)
    }

    fun getEvents(){
        eventsList.value = getEventsUseCase(selectedDate.value, selectedCalendarsId.value)
    }

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
        startForBottomSheet.value = LocalTime.now()
        endForBottomSheet.value = LocalTime.now().plusHours(1)
        allDayForBottomSheet.value = 0
        repeatRuleForBottomSheet.value = arrayOf("Нет", "")
        descriptionForBottomSheet.value = ""
        pickedDateForBottomSheet.value = selectedDate.value
        updaterBottomSheet.value = false
    }

    fun pickedEventForBottomSheet(id: String, calendarId: String, start: LocalDateTime, end: LocalDateTime){
        val event = getOneEventUseCase(id, calendarId, start, end)
        calendarIdForBottomSheet.value = event.calendarId
        calendarDisplayNameForBottomSheet.value = event.calendarDisplayName
        titleForBottomSheet.value =  event.title
        locationForBottomSheet.value =  event.location ?: ""
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
            updateEventUseCase(event)
        } else {
            insertEventUseCase(event)
        }
        getEvents()
    }

    fun deleteEvent(){
        deleteEventUseCase(idEventForBottomSheet.value)
        getEvents()
    }
}