package ru.simpleplanner.presentation.eventView

import android.Manifest
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.simpleplanner.domain.entities.Event
import ru.simpleplanner.presentation.EventVM
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalPermissionsApi
@Composable
fun eventActivity(eventVM: EventVM) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )
    )
    var openAlertDialog = remember { mutableStateOf(false) }
    if (openAlertDialog.value) {
        calendarsForList(openAlertDialog, eventVM)
    }
    var bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            skipPartiallyExpanded = true,
            initialValue = SheetValue.Hidden
        )
    )
    val scope = rememberCoroutineScope()
    if (permissionsState.allPermissionsGranted) {
        eventVM.permissionsGranted.value = true
        eventVM.getCalendars()
        eventVM.getEvents()
        scaffold(openAlertDialog, scope, bottomSheetScaffoldState, eventVM)
    } else {
        getPermissions(permissionsState)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun getPermissions(permissionsState: MultiplePermissionsState) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = {contentPadding ->
            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Добавьте разрешение для использования календаря.",
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                    Text("Дать разрешение")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
private fun scaffold(
    openAlertDialog: MutableState<Boolean>,
    scope: CoroutineScope,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    eventVM: EventVM
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = { settingsTopBar(openAlertDialog) },
        floatingActionButton = { addButton(scope, bottomSheetScaffoldState, eventVM) },
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            pickDay(eventVM)
            listEvents(eventVM.eventsList, scope, bottomSheetScaffoldState, eventVM)
        }
    }
    bottomSheet(scope, bottomSheetScaffoldState, eventVM)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun settingsTopBar(openAlertDialog: MutableState<Boolean>) {
    TopAppBar(
        title = {},
        colors =  TopAppBarDefaults.topAppBarColors(Color.Transparent),
        actions = {
            IconButton(onClick = { openAlertDialog.value = true })
            {
                Icon(Icons.Outlined.Settings, contentDescription = "Settings")
            }
        }
    )
}

@Composable
fun pickDay(eventVM: EventVM) {
    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("dd MMM yyyy", Locale("ru"))
                .format(eventVM.selectedDate.value)
        }
    }
    val dateDialogState = rememberMaterialDialogState()
    Row(
        modifier = Modifier
            .padding(32.dp, 0.dp, 32.dp, 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(text = formattedDate)
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
        },
        shape = RoundedCornerShape(24.dp)
    ) {
        datepicker(
            initialDate = eventVM.selectedDate.value,
            title = "",
            locale = Locale("ru")
        ) {
            eventVM.selectedDate.value = it
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun listEvents(
    events: MutableState<List<Event>>,
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    eventVM: EventVM
) {
    if (events.value.isNotEmpty()) {
        LazyColumn() {
            itemsIndexed(events.value.sortedBy { it.start }) { _, item ->
                Row(
                    modifier = Modifier
                        .height(64.dp)
                        .fillMaxWidth()
                        .padding(32.dp, 8.dp, 32.dp, 8.dp)
                        .clickable {
                            eventVM.pickedEventForBottomSheet(item.id, item.calendarId, item.start, item.end)
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = item.start.format(DateTimeFormatter.ofPattern("HH:mm"))
                                + "\n" +
                                item.end.format(DateTimeFormatter.ofPattern("HH:mm")),
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
        }
    } else {
        Column( modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "Событий на выбранный день нет",
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun addButton(
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    eventVM: EventVM
) {
    FloatingActionButton(
        modifier = Modifier.padding(all = 16.dp),
        onClick = {
            eventVM.newEventForBottomSheet()
            scope.launch {
                scaffoldState.bottomSheetState.expand()
            }
        }
    ) {
        Icon(Icons.Filled.Add, "Add event button")
    }
}