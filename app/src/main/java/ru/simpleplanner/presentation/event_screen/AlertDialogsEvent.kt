package ru.simpleplanner.presentation.event_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Job
import ru.simpleplanner.domain.entities.Calendar
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarDialogChooseTime(
    openDialog: MutableState<Boolean>,
    start: MutableState<LocalTime>,
    end: MutableState<LocalTime>,
    isTimePickerStart: MutableState<Boolean>){
   // val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val state = rememberTimePickerState(
        initialHour = if(isTimePickerStart.value) start.value.hour else end.value.hour,
        initialMinute = 0)
    TimePickerDialog(
        title = "",
        onConfirm = {
                openDialog.value = false
                if(isTimePickerStart.value){
                    start.value =
                        LocalTime.of(state.hour, state.minute)
                    if(end.value < start.value){
                        end.value =
                            start.value.plusHours(1)
                    }
                } else {
                 end.value =
                     LocalTime.of(state.hour, state.minute)
                 if(end.value < start.value) {
                     start.value = end.value.minusHours(1)
                 }
                }
            },
        onCancel  = {
            openDialog.value = false
        }
    ) {
        TimePicker(state = state)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarAlertDialogChooseDate(
    selectedDate: MutableState<LocalDate>,
    pickedDateForBottomSheet: MutableState<LocalDate>,
    openDialog: MutableState<Boolean>,
    isDatePickerInBottomSheet: Boolean
){
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate.value.atStartOfDay(ZoneOffset.of("Z")).toInstant().toEpochMilli())
    DatePickerDialog(
        onDismissRequest = { openDialog.value = false },
        confirmButton = {
            TextButton(
                onClick = {
                    openDialog.value = false
                    if (isDatePickerInBottomSheet){
                        pickedDateForBottomSheet.value =
                            datePickerState.selectedDateMillis?.let {
                                Instant.ofEpochMilli(it)
                                    .atZone(ZoneId.systemDefault()).toLocalDate()
                            }!!
                    } else {
                        selectedDate.value =
                            datePickerState.selectedDateMillis?.let {
                                Instant.ofEpochMilli(it)
                                    .atZone(ZoneId.systemDefault()).toLocalDate()
                            }!!
                    }
                },
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    openDialog.value = false
                }
            ) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            title = { },
            showModeToggle = false
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarAlertDialogListOfCalendars(
    openAlertDialog: MutableState<Boolean>,
    selectedCalendarsId: MutableState<MutableList<String>>,
    getEvents: Job,
    calendars: MutableState<List<Calendar>>) {
    AlertDialog(onDismissRequest = {
        openAlertDialog.value = false
    }) {
        Surface(
            modifier = Modifier
                .width(360.dp)
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                calendars.value.forEach { item ->
                    var checked by remember {
                        mutableStateOf(selectedCalendarsId.value.contains(item.id))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checked_ ->
                                checked = checked_
                                if (checked) {
                                    selectedCalendarsId.value.add(item.id)
                                } else {
                                    selectedCalendarsId.value.remove(item.id)
                                }
                                getEvents.start()
                            }
                        )
                        Text(
                            modifier = Modifier.padding(start = 2.dp),
                            text = item.displayName
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarAlertDialogListCalendarsForEvent(
    openAlertDialog: MutableState<Boolean>,
    calendarDisplayNameForBottomSheet: MutableState<String>,
    calendarIdForBottomSheet:  MutableState<String>,
    calendarsList: MutableState<List<Calendar>>
){
    val calendarNameTemporal = remember {
        mutableStateOf(calendarDisplayNameForBottomSheet.value)}
    val calendarIdTemporal = remember {
        mutableStateOf(calendarIdForBottomSheet.value)}
    AlertDialog(onDismissRequest = {
        openAlertDialog.value = false
        calendarIdTemporal.value = calendarIdForBottomSheet.value
        calendarNameTemporal.value = calendarDisplayNameForBottomSheet.value
    }) {
        Surface(
            modifier = Modifier
                .width(360.dp)
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                val (selectedOption, onOptionSelected) = remember {
                    mutableStateOf(
                        if(calendarIdForBottomSheet.value == ""){
                            calendarsList.value[0]
                        } else {
                            calendarsList.value.find {
                                it.id == calendarIdForBottomSheet.value
                            } ?: calendarsList.value[0]
                    }) }
                calendarsList.value.forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.selectableGroup()) {
                        RadioButton(
                            selected = (item == selectedOption),
                            onClick = {
                                onOptionSelected(item)
                                calendarIdTemporal.value = item.id
                                calendarNameTemporal.value = item.displayName
                            }
                        )
                        Text(
                            modifier = Modifier.padding(start = 2.dp),
                            text = item.displayName
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = {
                        openAlertDialog.value = false
                        calendarDisplayNameForBottomSheet.value = calendarNameTemporal.value
                        calendarIdForBottomSheet.value = calendarIdTemporal.value
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Применить")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarAlertDialogEventDescription(
    openAlertDialogDescription: MutableState<Boolean>,
    descriptionForBottomSheet: MutableState<String>
){
    val descriptionTemporal = remember {
        mutableStateOf(descriptionForBottomSheet.value) }
    AlertDialog(onDismissRequest = {
        openAlertDialogDescription.value = false
        descriptionTemporal.value = descriptionForBottomSheet.value
    }) {
        Surface(
            modifier = Modifier
                .width(360.dp)
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Описание")
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(

                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeightIn(min = 100.dp, max = 300.dp),
                    value = descriptionTemporal.value,
                    onValueChange = { descriptionTemporal.value = it },
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = {
                        openAlertDialogDescription.value = false
                        descriptionForBottomSheet.value = descriptionTemporal.value
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Применить")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarAlertDialogEventRepeatRule(
    openAlertDialogCalendars: MutableState<Boolean>,
    repeatRuleForBottomSheet: MutableState<Array<String>>,
    repeatRule: Array<Array<String>>
){
    val repeatRuleTemporal = remember {
        mutableStateOf(repeatRuleForBottomSheet.value) }
    AlertDialog(onDismissRequest = {
        openAlertDialogCalendars.value = false
        repeatRuleTemporal.value = repeatRuleForBottomSheet.value
    }) {
        Surface(
            modifier = Modifier
                .width(360.dp)
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                val (selectedOption, onOptionSelected) = remember {
                    mutableStateOf(repeatRuleTemporal.value)
                }
                repeatRule.forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.selectableGroup()) {
                        RadioButton(
                            selected = (item.contentEquals(selectedOption)),
                            onClick = {
                                onOptionSelected(item)
                                repeatRuleTemporal.value = item
                            }
                        )
                        Text(
                            modifier = Modifier.padding(start = 2.dp),
                            text = item[0]
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = {
                        openAlertDialogCalendars.value = false
                        repeatRuleForBottomSheet.value = repeatRuleTemporal.value
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Применить")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarAlertDialogEventLocation(
    openAlertDialog: MutableState<Boolean>,
    locationForBottomSheet: MutableState<String>,
){
    val locationTemporal = remember {
        mutableStateOf(locationForBottomSheet.value) }
    AlertDialog(onDismissRequest = {
        openAlertDialog.value = false
        locationTemporal.value = locationForBottomSheet.value
    }) {
        Surface(
            modifier = Modifier
                .width(360.dp)
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Местоположение")
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(

                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeightIn(min = 100.dp, max = 300.dp),
                    value = locationTemporal.value,
                    onValueChange = { locationTemporal.value = it },
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = {
                        openAlertDialog.value = false
                        locationForBottomSheet.value = locationTemporal.value
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Применить")
                }
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    toggle: @Composable () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    toggle()
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = onCancel
                    ) { Text("Cancel") }
                    TextButton(
                        onClick = onConfirm
                    ) { Text("OK") }
                }
            }
        }
    }
}