package ru.simpleplanner.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

    val eventVM: EventVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimplePlannerTheme {
                eventActivity(eventVM)
            }
        }
    }

    @ExperimentalPermissionsApi
    @Composable
    fun eventActivity(eventVM: EventVM) {
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            pickDay(eventVM)
            val permissionsState = rememberMultiplePermissionsState(
                permissions = listOf(
                    android.Manifest.permission.READ_CALENDAR,
                    android.Manifest.permission.WRITE_CALENDAR
                )
            )
            getPermissions(permissionsState, eventVM)
        }
    }

    @ExperimentalPermissionsApi
    @Composable
    private fun getPermissions(
        permissionsState: MultiplePermissionsState,
        eventVM: EventVM
    ) {
        if (permissionsState.allPermissionsGranted) {
            eventVM.permissionsGranted.value = true
            listCalendars()
        } else {
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
    }

    @Composable
    fun pickDay(eventVM: EventVM) {
        val formattedDate = remember {
            derivedStateOf {
                DateTimeFormatter
                    .ofPattern("dd mmm")
                    .format(eventVM.pickedDate.value)
            }
        }
        val dateDialogState = rememberMaterialDialogState()
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(text = eventVM.pickedDate.value.toString())
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
                }
                negativeButton(text = "Отмена")
            }
        ) {
            datepicker(
                initialDate = eventVM.pickedDate.value,
                title = ""
            ) {
                eventVM.pickedDate.value = it
            }
        }
    }

    @Composable
    fun listCalendars() {
        eventVM.getCalendars()
        var expanded by remember { mutableStateOf(false) }
        var selectedOptionText by remember { mutableStateOf("") }
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
                    value = selectedOptionText,
                    onValueChange = { selectedOptionText },
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
                if (eventVM.calendarList.value.isNotEmpty()) {
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        eventVM.calendarList.value.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(text = selectionOption.displayName) },
                                onClick = {
                                    selectedOptionText = selectionOption.displayName
                                    eventVM.pickedCalendar = selectionOption.displayName
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}