package ru.simpleplanner.presentation.eventView

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.simpleplanner.domain.entities.Event
import ru.simpleplanner.presentation.EventVM
import java.io.FileDescriptor
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun bottomSheet(
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    eventVM: EventVM
) {
    val calendarId = remember { mutableStateOf(
        if(!eventVM.calendars.value.isEmpty()){
            eventVM.calendars.value[0].id
        } else { "" }
    )}
    val calendarDisplayName = remember { mutableStateOf(
        if(!eventVM.calendars.value.isEmpty()){
            eventVM.calendars.value[0].displayName
        } else { "" }
    )}
    val title = remember { mutableStateOf("") }
    val location = remember { mutableStateOf("") }
    val start = remember { mutableStateOf(LocalTime.now()) }
    val end = remember { mutableStateOf(LocalTime.now().plusHours(1)) }
    val allDay = remember { mutableStateOf(0) }
    val repeatRule = remember { mutableStateOf(arrayOf("Нет", "")) }
    val description = remember { mutableStateOf("") }

    var openAlertDialogCalendars = remember { mutableStateOf(false) }
    var openAlertDialogDescription = remember { mutableStateOf(false) }
    var openAlertDialogRepeatRule = remember { mutableStateOf(false) }
    var openAlertDialogLocation = remember { mutableStateOf(false) }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetShadowElevation = 32.dp,
        sheetContent = { bottomSheetContent(
            scope,
            scaffoldState,
            eventVM,
            openAlertDialogCalendars,
            openAlertDialogDescription,
            openAlertDialogRepeatRule,
            openAlertDialogLocation,
            title,
            start,
            end,
            calendarDisplayName,
            calendarId,
            location,
            description,
            repeatRule,
            allDay
        ) }
    ) {}

    if(openAlertDialogCalendars.value) {
        calendarForEvent(openAlertDialogCalendars, eventVM, calendarId, calendarDisplayName)
    }

    if(openAlertDialogDescription.value) {
        descriptionForEvent(openAlertDialogDescription, description)
    }

    if(openAlertDialogRepeatRule.value) {
        repeatRuleForEvent(openAlertDialogRepeatRule, eventVM, repeatRule)
    }

    if(openAlertDialogLocation.value) {
        locationForEvent(openAlertDialogLocation, location)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun bottomSheetContent(
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    eventVM: EventVM,
    openAlertDialogCalendars: MutableState<Boolean>,
    openAlertDialogDescription: MutableState<Boolean>,
    openAlertDialogRepeatRule: MutableState<Boolean>,
    openAlertDialogLocation: MutableState<Boolean>,
    title: MutableState<String>,
    start: MutableState<LocalTime>,
    end: MutableState<LocalTime>,
    calendarDisplayName: MutableState<String>,
    calendarId : MutableState<String>,
    location : MutableState<String>,
    description: MutableState<String>,
    repeatRule : MutableState<Array<String>>,
    allDay: MutableState<Int>
) {

    var dateDialogState = rememberMaterialDialogState()
    val pickedDate = remember { mutableStateOf(LocalDate.now()) }

    var startTimeDialogState = rememberMaterialDialogState()

    var endTimeDialogState = rememberMaterialDialogState()

    Column(
        modifier = Modifier
            .height(500.dp)
            .fillMaxWidth()
            .padding(32.dp, 0.dp, 32.dp, 0.dp)
    ){
        titleEvent(title)
        Spacer(modifier = Modifier.padding(8.dp))
        dateAndTimeEvent(
            dateDialogState,
            startTimeDialogState,
            endTimeDialogState,
            start,
            end,
            pickedDate)
        Spacer(modifier = Modifier.padding(8.dp))
        pickAllDay(allDay)
        Spacer(modifier = Modifier.padding(8.dp))
        pickRepeatRule(repeatRule, openAlertDialogRepeatRule)
        Spacer(modifier = Modifier.padding(8.dp))
        pickCalendar(openAlertDialogCalendars, calendarDisplayName, eventVM)
        Spacer(modifier = Modifier.padding(8.dp))
        pickDescription(openAlertDialogDescription)
        Spacer(modifier = Modifier.padding(8.dp))
        pickLocation(openAlertDialogLocation)
        Spacer(modifier = Modifier.padding(8.dp))
        saveButton(
            scope,
            scaffoldState,
            eventVM,
            title,
            pickedDate,
            start,
            end,
            location,
            description,
            calendarDisplayName,
            calendarId,
            repeatRule,
            allDay
        )
    }
    pickDate(dateDialogState, pickedDate)
    pickStartTime(startTimeDialogState, start, end)
    pickEndTime(endTimeDialogState, start, end)
}

@Composable
fun titleEvent(title: MutableState<String>){
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ){
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = title.value,
            onValueChange = { title.value = it },
            label = { Text(text = "Введите название") },
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
    }
}

@Composable
fun dateAndTimeEvent(
    dateDialogState: MaterialDialogState,
    startTimeDialogState: MaterialDialogState,
    endTimeDialogState: MaterialDialogState,
    start: MutableState<LocalTime>,
    end: MutableState<LocalTime>,
    pickedDate: MutableState<LocalDate>
    ){
    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("dd LLL", Locale("ru"))
                .format(pickedDate.value)
        }
    }
    val formattedTimeStart by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("HH:mm")
                .format(start.value)
        }
    }
    val formattedTimeEnd by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("HH:mm")
                .format(end.value)
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center)
    {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = "Дата",
                modifier = Modifier.height(20.dp)
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Box(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(Color.Gray)
                    .height(64.dp)
                    .fillMaxWidth()
                    .clickable { dateDialogState.show() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formattedDate,
                    textAlign = TextAlign.Center)
            }
        }

        Spacer(modifier = Modifier.padding(8.dp))
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = "Начало",
                modifier = Modifier.height(20.dp)
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Box(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(Color.Gray)
                    .height(64.dp)
                    .fillMaxWidth()
                    .clickable { startTimeDialogState.show() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formattedTimeStart,
                    textAlign = TextAlign.Center)
            }
        }

        Spacer(modifier = Modifier.padding(8.dp))
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = "Конец",
                modifier = Modifier.height(20.dp)
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Box(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(Color.Gray)
                    .height(64.dp)
                    .fillMaxWidth()
                    .clickable { endTimeDialogState.show() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formattedTimeEnd,
                    textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun pickDate(
    dateDialogState: MaterialDialogState,
    pickedDate: MutableState<LocalDate>
){
    var pickedDateTemporal = pickedDate.value
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(text = "ОК") {
                pickedDate.value = pickedDateTemporal
            }
            negativeButton(text = "Отмена")
        },
        shape = RoundedCornerShape(24.dp)
    ) {
        datepicker(
            initialDate = pickedDateTemporal,
            title = "",
            locale = Locale("ru")
        ) {
           pickedDateTemporal = it
        }
    }
}

@Composable
fun pickStartTime(
    startTimeDialogState: MaterialDialogState,
    start: MutableState<LocalTime>,
    end: MutableState<LocalTime>
){
    var pickedTimeTemporal = start.value
    MaterialDialog(
        dialogState = startTimeDialogState,
        buttons = {
            positiveButton(text = "ОК") {
                start.value = pickedTimeTemporal
                if(start.value > end.value){
                    end.value = start.value
                }
            }
            negativeButton(text = "Отмена")
        }, 
        shape = RoundedCornerShape(24.dp),
        content = {
            Spacer(modifier = Modifier.padding(16.dp))
            timepicker(
                initialTime = start.value,
                title = "",
                is24HourClock = true
            ) {
                pickedTimeTemporal = it
            }
        }
    )
}

@Composable
fun pickEndTime(
    endTimeDialogState: MaterialDialogState,
    start: MutableState<LocalTime>,
    end: MutableState<LocalTime>
){
    var pickedTimeTemporal = end.value
    MaterialDialog(
        dialogState = endTimeDialogState,
        buttons = {
            positiveButton(text = "ОК") {
                end.value = pickedTimeTemporal
                if(start.value > end.value){
                    start.value = end.value
                }
            }
            negativeButton(text = "Отмена")
        },
        shape = RoundedCornerShape(24.dp),
        content = {
            Spacer(modifier = Modifier.padding(16.dp))
            timepicker(
                initialTime = end.value,
                title = "",
                is24HourClock = true
            ) {
                pickedTimeTemporal = it
            }
        }
    )
}

@Composable
fun pickRepeatRule(
    repeatRule: MutableState<Array<String>>,
    openAlertDialogRepeatRule: MutableState<Boolean>
){
    Row(modifier = Modifier.fillMaxWidth()){
        Text(
            text = "Повтор",
            modifier = Modifier
                .weight(5f),
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.padding(2.dp))
        Row( modifier = Modifier
            .weight(6f)
            .clickable { openAlertDialogRepeatRule.value = true },
            horizontalArrangement = Arrangement.End) {
            Text(
                text = repeatRule.value[0],
                textAlign = TextAlign.End
            )
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Choose repeat"
            )
        }
    }
}

@Composable
fun pickAllDay(
    allDay: MutableState<Int>
){
    Row( modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = "Весь день",
            modifier = Modifier
                .weight(5f),
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.padding(2.dp))
        Row( modifier = Modifier
            .weight(6f),
            horizontalArrangement = Arrangement.End) {
            var checked by remember { mutableStateOf(allDay.value.toString().toBoolean()) }
            Switch(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    if(it == true) { allDay.value = 1 }
                    else { allDay.value = 0 }
                })
        }
    }
}

@Composable
fun pickCalendar(
    openAlertDialogCalendars: MutableState<Boolean>,
    calendarDisplayName: MutableState<String>,
    eventVM: EventVM
){
    Row(modifier = Modifier.fillMaxWidth()){
        Text(
            text = "Календарь",
            modifier = Modifier
                .weight(5f),
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.padding(2.dp))
        Row(modifier = Modifier
            .weight(6f)
            .clickable { openAlertDialogCalendars.value = true },
            horizontalArrangement = Arrangement.End) {
            Text(
                text = calendarDisplayName.value,
                textAlign = TextAlign.End
            )
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Choose calendar"
            )
        }
    }
}

@Composable
fun pickDescription(openAlertDialogDescription: MutableState<Boolean>){
    Row(modifier = Modifier.fillMaxWidth()){
        Text(
            text = "Описание",
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(5f)
        )
        Row(modifier = Modifier
            .weight(6f)
            .clickable { openAlertDialogDescription.value = true },
            horizontalArrangement = Arrangement.End
        ){
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Write description"
            )
        }
    }
}

@Composable
fun pickLocation(
    openAlertDialogLocation: MutableState<Boolean>
){
    Row(modifier = Modifier.fillMaxWidth()){
        Text(
            text = "Местоположение",
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(5f)
        )
        Row(modifier = Modifier
            .weight(6f)
            .clickable { openAlertDialogLocation.value = true },
            horizontalArrangement = Arrangement.End
        ){
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Write location"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun saveButton(
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    eventVM: EventVM,
    title: MutableState<String>,
    date: MutableState<LocalDate>,
    start: MutableState<LocalTime>,
    end: MutableState<LocalTime>,
    location : MutableState<String>,
    description : MutableState<String>,
    calendarDisplayName : MutableState<String>,
    calendarId: MutableState<String>,
    repeatRule : MutableState<Array<String>>,
    allDay: MutableState<Int>
) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        val startDateTime =
            date.value.atStartOfDay(ZoneOffset.systemDefault())
                .toInstant().plusSeconds(start.value.toSecondOfDay().toLong()).toEpochMilli()
        val endDateTime =
            date.value.atStartOfDay(ZoneOffset.systemDefault())
                .toInstant().plusSeconds(end.value.toSecondOfDay().toLong()).toEpochMilli()
        val event = Event(
            calendarId.value,
            calendarDisplayName.value,
            title.value,
            location.value,
            startDateTime,
            endDateTime,
            allDay.value,
            repeatRule.value[1],
            description.value,
            TimeZone.getDefault().toString()
        )
        Button(
            modifier = Modifier
                .height(48.dp)
                .width(200.dp),
            shape = RoundedCornerShape(36.dp),
            onClick = {
                eventVM.saveEvent(event)
                scope.launch {
                    scaffoldState.bottomSheetState.hide()
                }

                calendarId.value =
                    if(!eventVM.calendars.value.isEmpty()){
                        eventVM.calendars.value[0].id
                    } else { "" }

                calendarDisplayName.value =
                    if(!eventVM.calendars.value.isEmpty()){
                        eventVM.calendars.value[0].displayName
                    } else { "" }

                title.value = ""
                location.value = ""
                start.value = LocalTime.now()
                end.value = LocalTime.now().plusHours(1)
                allDay.value = 0
                repeatRule.value[0] = "Нет"
                repeatRule.value[1] = ""
                description.value = ""
            }
        ){
            Text(text = "Добавить")
        }
    }
}