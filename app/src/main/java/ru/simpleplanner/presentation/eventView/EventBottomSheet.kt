package ru.simpleplanner.presentation.eventView

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun bottomSheet(
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    eventVM: EventVM
) {
    val openAlertDialogCalendars = remember { mutableStateOf(false) }
    val openAlertDialogDescription = remember { mutableStateOf(false) }
    val openAlertDialogRepeatRule = remember { mutableStateOf(false) }
    val openAlertDialogLocation = remember { mutableStateOf(false) }

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
            openAlertDialogLocation
        ) }
    ) {}

    if(openAlertDialogCalendars.value) {
        calendarForEvent(openAlertDialogCalendars, eventVM)
    }

    if(openAlertDialogDescription.value) {
        descriptionForEvent(openAlertDialogDescription, eventVM)
    }

    if(openAlertDialogRepeatRule.value) {
        repeatRuleForEvent(openAlertDialogRepeatRule, eventVM)
    }

    if(openAlertDialogLocation.value) {
        locationForEvent(openAlertDialogLocation, eventVM)
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
    openAlertDialogLocation: MutableState<Boolean>
) {

    val dateDialogState = rememberMaterialDialogState()

    val startTimeDialogState = rememberMaterialDialogState()

    val endTimeDialogState = rememberMaterialDialogState()

    Column(
        modifier = Modifier
            .height(500.dp)
            .fillMaxWidth()
            .padding(32.dp, 0.dp, 32.dp, 0.dp)
    ){
        titleEvent(eventVM)
        Spacer(modifier = Modifier.padding(8.dp))
        dateAndTimeEvent(
            dateDialogState,
            startTimeDialogState,
            endTimeDialogState,
            eventVM
        )
        Spacer(modifier = Modifier.padding(8.dp))
        pickAllDay(eventVM)
        Spacer(modifier = Modifier.padding(8.dp))
        pickRepeatRule(eventVM, openAlertDialogRepeatRule)
        Spacer(modifier = Modifier.padding(8.dp))
        pickCalendar(openAlertDialogCalendars, eventVM)
        Spacer(modifier = Modifier.padding(8.dp))
        pickDescription(openAlertDialogDescription)
        Spacer(modifier = Modifier.padding(8.dp))
        pickLocation(openAlertDialogLocation)
        Spacer(modifier = Modifier.padding(8.dp))
        button(scope, scaffoldState, eventVM)
    }
    pickDate(dateDialogState, eventVM)
    pickStartTime(startTimeDialogState, eventVM)
    pickEndTime(endTimeDialogState, eventVM)
}

@Composable
fun titleEvent(eventVM: EventVM){
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ){
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = eventVM.titleForBottomSheet.value,
            onValueChange = { eventVM.titleForBottomSheet.value = it },
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
    eventVM: EventVM
    ){
    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("dd LLL", Locale("ru"))
                .format(eventVM.pickedDateForBottomSheet.value)
        }
    }
    val formattedTimeStart by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("HH:mm")
                .format(eventVM.startForBottomSheet.value)
        }
    }
    val formattedTimeEnd by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("HH:mm")
                .format(eventVM.endForBottomSheet.value)
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
                    textAlign = TextAlign.Center
                )
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
            if(eventVM.allDayForBottomSheet.value == 0) {
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
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(Color.DarkGray)
                        .height(64.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formattedTimeStart,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
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
            if(eventVM.allDayForBottomSheet.value == 0) {
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
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(Color.DarkGray)
                        .height(64.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formattedTimeEnd,
                        textAlign = TextAlign.Center,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun pickDate(
    dateDialogState: MaterialDialogState,
    eventVM: EventVM
){
    var pickedDateTemporal = eventVM.pickedDateForBottomSheet.value
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(text = "ОК") {
                eventVM.pickedDateForBottomSheet.value = pickedDateTemporal
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
    eventVM: EventVM
){
    var pickedTimeTemporal = eventVM.startForBottomSheet.value
    MaterialDialog(
        dialogState = startTimeDialogState,
        buttons = {
            positiveButton(text = "ОК") {
                eventVM.startForBottomSheet.value = pickedTimeTemporal
                if(eventVM.startForBottomSheet.value > eventVM.endForBottomSheet.value){
                    eventVM.endForBottomSheet.value = eventVM.startForBottomSheet.value
                }
            }
            negativeButton(text = "Отмена")
        }, 
        shape = RoundedCornerShape(24.dp),
        content = {
            Spacer(modifier = Modifier.padding(16.dp))
            timepicker(
                initialTime = eventVM.startForBottomSheet.value,
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
    eventVM: EventVM
){
    var pickedTimeTemporal = eventVM.endForBottomSheet.value
    MaterialDialog(
        dialogState = endTimeDialogState,
        buttons = {
            positiveButton(text = "ОК") {
                eventVM.endForBottomSheet.value = pickedTimeTemporal
                if(eventVM.startForBottomSheet.value > eventVM.endForBottomSheet.value){
                    eventVM.startForBottomSheet.value = eventVM.endForBottomSheet.value
                }
            }
            negativeButton(text = "Отмена")
        },
        shape = RoundedCornerShape(24.dp),
        content = {
            Spacer(modifier = Modifier.padding(16.dp))
            timepicker(
                initialTime = eventVM.endForBottomSheet.value,
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
    eventVM: EventVM,
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
                text = eventVM.repeatRuleForBottomSheet.value[0],
                textAlign = TextAlign.End
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = "Choose repeat"
            )
        }
    }
}

@Composable
fun pickAllDay(
    eventVM: EventVM
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
        Row(
            modifier = Modifier.weight(6f),
            horizontalArrangement = Arrangement.End
        ) {
            val checked by remember {
                mutableStateOf(eventVM.allDayForBottomSheet)}
            Switch(
                checked = if(checked.value == 1) true else false,
                onCheckedChange = {
                    checked.value = if(it) 1 else 0
                    if(it) eventVM.allDayForBottomSheet.value = 1
                    else eventVM.allDayForBottomSheet.value = 0
                })
        }
    }
}

@Composable
fun pickCalendar(
    openAlertDialogCalendars: MutableState<Boolean>,
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
                text = eventVM.calendarDisplayNameForBottomSheet.value,
                textAlign = TextAlign.End,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
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
                imageVector = Icons.Outlined.KeyboardArrowRight,
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
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = "Write location"
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun button(
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    eventVM: EventVM
) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            modifier = Modifier
                .height(48.dp)
                .width(200.dp),
            shape = RoundedCornerShape(36.dp),
            onClick = {
                eventVM.saveOrUpdateEvent()
                scope.launch {
                    scaffoldState.bottomSheetState.hide()
                }
            }
        ){
            if(eventVM.updaterBottomSheet.value) {
                Text(text = "Изменить")
            } else {
                Text(text = "Добавить")
            }
        }
    }
}