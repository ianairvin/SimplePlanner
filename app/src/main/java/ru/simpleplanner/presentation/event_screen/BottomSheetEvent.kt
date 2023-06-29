package ru.simpleplanner.presentation.event_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.TimePickerDefaults
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.simpleplanner.presentation.ui.theme.*
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarBottomSheet(
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
        containerColor = colorScheme.background,
        sheetShadowElevation = 0.dp,
        sheetContent = { CalendarBottomSheetEventContent(
            scope,
            scaffoldState,
            eventVM,
            openAlertDialogCalendars,
            openAlertDialogDescription,
            openAlertDialogRepeatRule,
            openAlertDialogLocation
        ) }
    ){}

    if(openAlertDialogCalendars.value) {
        CalendarAlertDialogListCalendarsForEvent(openAlertDialogCalendars, eventVM)
    }

    if(openAlertDialogDescription.value) {
        CalendarAlertDialogEventDescription(openAlertDialogDescription, eventVM)
    }

    if(openAlertDialogRepeatRule.value) {
        CalendarAlertDialogEventRepeatRule(openAlertDialogRepeatRule, eventVM)
    }

    if(openAlertDialogLocation.value) {
        CalendarAlertDialogEventLocation(openAlertDialogLocation, eventVM)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarBottomSheetEventContent(
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    eventVM: EventVM,
    openAlertDialogCalendars: MutableState<Boolean>,
    openAlertDialogDescription: MutableState<Boolean>,
    openAlertDialogRepeatRule: MutableState<Boolean>,
    openAlertDialogLocation: MutableState<Boolean>
) {

    val interactionSource = MutableInteractionSource()

    val dateDialogState = rememberMaterialDialogState()

    val startTimeDialogState = rememberMaterialDialogState()

    val endTimeDialogState = rememberMaterialDialogState()

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(32.dp, 0.dp, 32.dp, 0.dp)
    ){
        CalendarBottomSheetEventTitle(eventVM)
        Spacer(modifier = Modifier.padding(8.dp))
        CalendarBottomSheetEventDateAndTime(
            dateDialogState,
            startTimeDialogState,
            endTimeDialogState,
            eventVM
        )
        Spacer(modifier = Modifier.padding(8.dp))
        CalendarBottomSheetEventAllDay(eventVM)
        Spacer(modifier = Modifier.padding(8.dp))
        CalendarBottomSheetEventRepeatRule(eventVM, openAlertDialogRepeatRule, interactionSource)
        Spacer(modifier = Modifier.padding(8.dp))
        CalendarBottomSheetEventCalendar(openAlertDialogCalendars, eventVM, interactionSource)
        Spacer(modifier = Modifier.padding(8.dp))
        CalendarBottomSheetEventDescription(openAlertDialogDescription, interactionSource)
        Spacer(modifier = Modifier.padding(8.dp))
        CalendarBottomSheetEventLocation(openAlertDialogLocation, interactionSource)
        Spacer(modifier = Modifier.padding(8.dp))
        CalendarBottomSheetButtons(scope, scaffoldState, eventVM)
    }
    CalendarBottomSheetEventDatePicker(dateDialogState, eventVM)
    CalendarBottomSheetEventStartTimePicker(startTimeDialogState, eventVM)
    CalendarBottomSheetEventEndTimePicker(endTimeDialogState, eventVM)
}

@Composable
fun CalendarBottomSheetEventTitle(eventVM: EventVM){
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

@SuppressLint("AutoboxingStateValueProperty")
@Composable
fun CalendarBottomSheetEventDateAndTime(
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
                modifier = Modifier.height(20.dp),
                fontWeight = FontWeight.W700
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Box(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(colorScheme.outlineVariant)
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
                modifier = Modifier.height(20.dp),
                fontWeight = FontWeight.W700
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Box(
                modifier = if(eventVM.allDayForBottomSheet.value == 0) {
                    Modifier
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(colorScheme.outlineVariant)
                        .height(64.dp)
                        .fillMaxWidth()
                        .clickable { startTimeDialogState.show() }
                } else {
                    Modifier
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(colorScheme.outlineVariant)
                        .height(64.dp)
                        .fillMaxWidth()
                       },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if(eventVM.allDayForBottomSheet.value == 0) formattedTimeStart else "-",
                    textAlign = TextAlign.Center,
                    color = if(eventVM.allDayForBottomSheet.value == 0)
                        colorScheme.onBackground else colorScheme.outline
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
                text = "Конец",
                modifier = Modifier.height(20.dp),
                fontWeight = FontWeight.W700
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Box(
                modifier = if(eventVM.allDayForBottomSheet.value == 0) {
                    Modifier
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(colorScheme.outlineVariant)
                        .height(64.dp)
                        .fillMaxWidth()
                        .clickable { endTimeDialogState.show() }
                } else {
                    Modifier
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(colorScheme.outlineVariant)
                        .height(64.dp)
                        .fillMaxWidth()
                },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if(eventVM.allDayForBottomSheet.value == 0) formattedTimeEnd else "-",
                    textAlign = TextAlign.Center,
                    color = if(eventVM.allDayForBottomSheet.value == 0)
                        colorScheme.onBackground else colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun CalendarBottomSheetEventDatePicker(
    dateDialogState: MaterialDialogState,
    eventVM: EventVM
){
    var pickedDateTemporal = eventVM.pickedDateForBottomSheet.value
    MaterialDialog(
        dialogState = dateDialogState,
        backgroundColor = colorScheme.background,
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
            locale = Locale("ru"),
            colors = DatePickerDefaults.colors(
                dateActiveBackgroundColor = colorScheme.primary,
                dateInactiveTextColor = colorScheme.onBackground,
                headerBackgroundColor = colorScheme.primary,
                headerTextColor = if(isSystemInDarkTheme()) colorScheme.onBackground else colorScheme.onPrimary,
                calendarHeaderTextColor = colorScheme.onBackground,
                dateActiveTextColor = if(isSystemInDarkTheme()) colorScheme.onBackground else colorScheme.onPrimary,
            )
        ) {
           pickedDateTemporal = it
        }
    }
}

@Composable
fun CalendarBottomSheetEventStartTimePicker(
    startTimeDialogState: MaterialDialogState,
    eventVM: EventVM
){
    var pickedTimeTemporal = eventVM.startForBottomSheet.value
    MaterialDialog(
        dialogState = startTimeDialogState,
        backgroundColor = colorScheme.background,
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
                is24HourClock = true,
                colors = TimePickerDefaults.colors(
                    headerTextColor = colorScheme.onPrimary,
                    inactivePeriodBackground = colorScheme.surface,
                    inactiveBackgroundColor = colorScheme.surface,
                    activeBackgroundColor = colorScheme.primary,
                    activeTextColor = colorScheme.onBackground,
                    inactiveTextColor = colorScheme.onBackground,
                    selectorColor = colorScheme.primaryContainer,
                    selectorTextColor = colorScheme.onBackground
                )
            ) {
                pickedTimeTemporal = it
            }
        }
    )
}

@Composable
fun CalendarBottomSheetEventEndTimePicker(
    endTimeDialogState: MaterialDialogState,
    eventVM: EventVM
){
    var pickedTimeTemporal = eventVM.endForBottomSheet.value
    MaterialDialog(
        dialogState = endTimeDialogState,
        backgroundColor = colorScheme.background,
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
                is24HourClock = true,
                colors = TimePickerDefaults.colors(
                    headerTextColor = colorScheme.onPrimary,
                    inactivePeriodBackground = colorScheme.surface,
                    inactiveBackgroundColor = colorScheme.surface,
                    activeBackgroundColor = colorScheme.primary,
                    activeTextColor = colorScheme.onBackground,
                    inactiveTextColor = colorScheme.onBackground,
                    selectorColor = colorScheme.primaryContainer,
                    selectorTextColor = colorScheme.onBackground
                )
            ) {
                pickedTimeTemporal = it
            }
        }
    )
}

@Composable
fun CalendarBottomSheetEventRepeatRule(
    eventVM: EventVM,
    openAlertDialogRepeatRule: MutableState<Boolean>,
    interactionSource: MutableInteractionSource
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
            .weight(6f).
            clickable(
                interactionSource = interactionSource,
                onClick = {openAlertDialogRepeatRule.value = true },
                indication = null
            ),
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

@SuppressLint("AutoboxingStateValueProperty")
@Composable
fun CalendarBottomSheetEventAllDay(
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
                checked = checked.value == 1,
                onCheckedChange = {
                    checked.value = if(it) 1 else 0
                    if(it) eventVM.allDayForBottomSheet.value = 1
                    else eventVM.allDayForBottomSheet.value = 0
                })
        }
    }
}

@Composable
fun CalendarBottomSheetEventCalendar(
    openAlertDialogCalendars: MutableState<Boolean>,
    eventVM: EventVM,
    interactionSource: MutableInteractionSource
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
            .clickable(
                interactionSource = interactionSource,
                onClick = { openAlertDialogCalendars.value = true },
                indication = null
            ),
            horizontalArrangement = Arrangement.End) {
            Text(
                text = eventVM.calendarDisplayNameForBottomSheet.value,
                textAlign = TextAlign.End,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier
                    .weight(5f)
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = "Choose calendar",
                modifier = Modifier
                    .weight(1f)
            )
        }
    }
}

@Composable
fun CalendarBottomSheetEventDescription(
    openAlertDialogDescription: MutableState<Boolean>,
    interactionSource: MutableInteractionSource
){
    Row(modifier = Modifier.fillMaxWidth()){
        Text(
            text = "Описание",
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(5f)
        )
        Row(modifier = Modifier
            .weight(6f)
            .clickable(
                interactionSource = interactionSource,
                onClick = { openAlertDialogDescription.value = true },
                indication = null
            ),
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
fun CalendarBottomSheetEventLocation(
    openAlertDialogLocation: MutableState<Boolean>,
    interactionSource: MutableInteractionSource
){
    Row(modifier = Modifier.fillMaxWidth()){
        Text(
            text = "Местоположение",
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(7f)
        )
        Row(modifier = Modifier
            .weight(3f)
            .clickable(
                interactionSource = interactionSource,
                onClick = { openAlertDialogLocation.value = true },
                indication = null
            ),
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
fun CalendarBottomSheetButtons(
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    eventVM: EventVM
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp, 16.dp, 16.dp, 56.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        if(eventVM.updaterBottomSheet.value) {
            Button(
                modifier = Modifier
                    .height(48.dp)
                    .weight(1f),
                shape = RoundedCornerShape(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.errorContainer,
                    contentColor = colorScheme.onErrorContainer
                ),
                onClick = {
                    eventVM.deleteEvent()
                    scope.launch {
                        scaffoldState.bottomSheetState.hide()
                    }
                }
            ) {
                Text(text = "Удалить")
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Button(
                modifier = Modifier
                    .height(48.dp)
                    .weight(1f),
                shape = RoundedCornerShape(36.dp),
                onClick = {
                    eventVM.saveOrUpdateEvent()
                    scope.launch {
                        scaffoldState.bottomSheetState.hide()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = md_theme_light_onPrimary
                )
            ) {
                Text(text = "Изменить")
            }
        } else {
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
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = md_theme_light_onPrimary
                )
            ) {
                Text(text = "Добавить")
            }
        }
    }
}