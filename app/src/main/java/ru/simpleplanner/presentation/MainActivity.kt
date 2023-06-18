package ru.simpleplanner.presentation

import android.graphics.drawable.Icon
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import dagger.hilt.android.AndroidEntryPoint
import ru.simpleplanner.presentation.ui.theme.SimplePlannerTheme
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
@OptIn(ExperimentalPermissionsApi::class)
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {

    private val eventVM: EventVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimplePlannerTheme {
                eventActivity()
            }
        }
    }

    @ExperimentalPermissionsApi
    @Composable
    private fun eventActivity() {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            eventVM.getCalendars()
            eventVM.getEvents()
            settings()
            pickDay()
            val permissionsState = rememberMultiplePermissionsState(
                permissions = listOf(
                    android.Manifest.permission.READ_CALENDAR,
                    android.Manifest.permission.WRITE_CALENDAR
                )
            )
            if (permissionsState.allPermissionsGranted) {
                eventVM.permissionsGranted.value = true
               // listCalendars()
                listEvents()
            } else {
                getPermissions(permissionsState)
            }
        }
    }

    @ExperimentalMaterial3Api
    @Composable
    private fun settings(){
        var openDialog = remember { mutableStateOf(false)}
        Column() {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .height(36.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { openDialog.value = true })
                {
                    Icon(Icons.Outlined.Settings, contentDescription = "Settings")
                }
            }
            if(openDialog.value == true){
                AlertDialog(onDismissRequest = { openDialog.value = false }) {
                    Surface(
                        modifier = Modifier
                            .width(360.dp)
                            .wrapContentHeight(),
                        shape = MaterialTheme.shapes.large,
                        tonalElevation = AlertDialogDefaults.TonalElevation
                        ) {
                        Column(modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                                eventVM.calendars.value.forEach { item ->
                                    var checked by remember {
                                        mutableStateOf(eventVM.selectedCalendarsId.contains(item.id))
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Checkbox(
                                            checked = checked,
                                            onCheckedChange = { checked_ ->
                                                checked = checked_
                                                if(checked == true) {
                                                    eventVM.selectedCalendarsId.add(item.id)
                                                } else {
                                                    eventVM.selectedCalendarsId.remove(item.id)
                                                }
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
                                    openDialog.value = false
                                    eventVM.getEvents()
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Применить")
                            }
                        }
                    }
                }
            }
        }
    }

    @ExperimentalPermissionsApi
    @Composable
    private fun getPermissions(permissionsState: MultiplePermissionsState) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Добавьте разрешение для использования календаря.")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                Text("Дать разрешение")
            }
        }
    }

    @Composable
    fun pickDay() {
        val formattedDate = remember {
            derivedStateOf {
                DateTimeFormatter
                    .ofPattern("dd mmm")
                    .format(eventVM.selectedDate.value)
            }
        }
        val dateDialogState = rememberMaterialDialogState()
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(text = eventVM.selectedDate.value.toString())
            Spacer(modifier = Modifier.padding(8.dp))
            Button(onClick = {
                dateDialogState.show()
            }) {
                Text(text = "Выбрать дату")
            }
        }
        MaterialDialog(
            dialogState = dateDialogState,
            buttons = {
                positiveButton(text = "ОК") {
                    eventVM.getEvents()
                }
                negativeButton(text = "Отмена")
            }
        ) {
            datepicker(
                initialDate = eventVM.selectedDate.value,
                title = "",
            ) {
                eventVM.selectedDate.value = it
            }
        }
    }

  /*  @Composable
    fun listCalendars() {
        eventVM.getCalendars()
        var expanded by remember { mutableStateOf(false) }
        var selectedCalendar by remember { mutableStateOf("") }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                TextField(
                    value = selectedCalendar,
                    onValueChange = { selectedCalendar },
                    readOnly = true,
                    label = { Text("Выберите календарь") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier.menuAnchor()
                )
                if (eventVM.calendars.value.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        eventVM.calendars.value.forEach { calendarItem ->
                            DropdownMenuItem(
                                text = { Text(text = calendarItem.displayName) },
                                onClick = {
                                    selectedCalendar = calendarItem.displayName
                                    eventVM.selectedCalendarName.value = calendarItem.displayName
                                    eventVM.selectedCalendarId.value = calendarItem.id
                                    eventVM.getEvents()
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    } */

    @Composable
    fun listEvents(){
        LazyColumn() {
            itemsIndexed(eventVM.events.value) { _, item ->
                Row(
                    modifier = Modifier
                        .height(64.dp)
                        .fillMaxWidth()
                        .padding(16.dp, 8.dp, 16.dp, 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    Text(
                        text = item.start.toString() + "\n" + item.end.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(16.dp))
                            .fillMaxHeight()
                            .background(Color.Gray)
                            .weight(4f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.title,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } // добавить else
    }
}