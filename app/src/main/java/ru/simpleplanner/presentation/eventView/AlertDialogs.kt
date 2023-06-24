package ru.simpleplanner.presentation.eventView

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.simpleplanner.presentation.EventVM

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun calendarsForList(openAlertDialog: MutableState<Boolean>, eventVM: EventVM) {
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
                eventVM.calendarsList.value.forEach { item ->
                    var checked by remember {
                        mutableStateOf(eventVM.selectedCalendarsId.value.contains(item.id))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { checked_ ->
                                checked = checked_
                                if (checked) {
                                    eventVM.selectedCalendarsId.value.add(item.id)
                                } else {
                                    eventVM.selectedCalendarsId.value.remove(item.id)
                                }
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
fun calendarForEvent(
    openAlertDialogCalendars: MutableState<Boolean>,
    eventVM: EventVM
){
    var calendarNameTemporal = remember {
        mutableStateOf(eventVM.calendarDisplayNameForBottomSheet.value)}
    var calendarIdTemporal = remember {
        mutableStateOf(eventVM.calendarIdForBottomSheet.value)}
    AlertDialog(onDismissRequest = {
        openAlertDialogCalendars.value = false
        calendarIdTemporal.value = eventVM.calendarIdForBottomSheet.value
        calendarNameTemporal.value = eventVM.calendarDisplayNameForBottomSheet.value
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
                        if(eventVM.calendarIdForBottomSheet.value == ""){
                            eventVM.calendarsList.value[0]
                        } else {
                            eventVM.calendarsList.value.find {
                                it.id == eventVM.calendarIdForBottomSheet.value
                            } ?: eventVM.calendarsList.value[0]
                    }) }
                eventVM.calendarsList.value.forEach { item ->
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
                        openAlertDialogCalendars.value = false
                        eventVM.calendarDisplayNameForBottomSheet.value = calendarNameTemporal.value
                        eventVM.calendarIdForBottomSheet.value = calendarIdTemporal.value
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
fun descriptionForEvent(
    openAlertDialogDescription: MutableState<Boolean>,
    eventVM: EventVM
){
    val descriptionTemporal = remember {
        mutableStateOf(eventVM.descriptionForBottomSheet.value) }
    AlertDialog(onDismissRequest = {
        openAlertDialogDescription.value = false
        descriptionTemporal.value = eventVM.descriptionForBottomSheet.value
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
                        eventVM.descriptionForBottomSheet.value = descriptionTemporal.value
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
fun repeatRuleForEvent(
    openAlertDialogCalendars: MutableState<Boolean>,
    eventVM: EventVM
){
    var repeatRuleTemporal = remember {
        mutableStateOf(eventVM.repeatRuleForBottomSheet.value) }
    AlertDialog(onDismissRequest = {
        openAlertDialogCalendars.value = false
        repeatRuleTemporal.value = eventVM.repeatRuleForBottomSheet.value
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
                eventVM.repeatRule.forEach { item ->
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
                        eventVM.repeatRuleForBottomSheet.value = repeatRuleTemporal.value
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
fun locationForEvent(
    openAlertDialogDescription: MutableState<Boolean>,
    eventVM: EventVM
){
    val locationTemporal = remember {
        mutableStateOf(eventVM.locationForBottomSheet.value) }
    AlertDialog(onDismissRequest = {
        openAlertDialogDescription.value = false
        locationTemporal.value = eventVM.locationForBottomSheet.value
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
                        openAlertDialogDescription.value = false
                        eventVM.locationForBottomSheet.value = locationTemporal.value
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Применить")
                }
            }
        }
    }
}