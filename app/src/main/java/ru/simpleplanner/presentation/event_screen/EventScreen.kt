package ru.simpleplanner.presentation.event_screen

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.simpleplanner.R
import ru.simpleplanner.domain.entities.Event
import ru.simpleplanner.domain.entities.Task
import ru.simpleplanner.presentation.ui.theme.md_theme_light_onPrimary
import ru.simpleplanner.presentation.ui.theme.priority_blue
import ru.simpleplanner.presentation.ui.theme.priority_green
import ru.simpleplanner.presentation.ui.theme.priority_purple
import ru.simpleplanner.presentation.ui.theme.priority_red
import ru.simpleplanner.presentation.ui.theme.priority_yellow
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalPermissionsApi
@Composable
fun EventActivity(eventVM: EventVM, onClickTask: () -> Unit, onClickTimer: () -> Unit) {
    val openAlertDialogListOfCalendars = remember { mutableStateOf(false) }
    if (openAlertDialogListOfCalendars.value) {
        CalendarAlertDialogListOfCalendars(openAlertDialogListOfCalendars, eventVM, eventVM.calendarsList)
    }
    val openDialogDatePicker = remember { mutableStateOf(false) }
    if(openDialogDatePicker.value) {
        CalendarAlertDialogChooseDate(eventVM, openDialogDatePicker, false)
    }

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            skipPartiallyExpanded = true,
            initialValue = SheetValue.Hidden,
            confirmValueChange = {true}
        )
    )
    val scope = rememberCoroutineScope()

    CalendarScaffold(
        openAlertDialogListOfCalendars,
        openDialogDatePicker,
        scope,
        bottomSheetScaffoldState,
        eventVM,
        onClickTask,
        onClickTimer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarScaffold(
    openAlertDialogListOfCalendars: MutableState<Boolean>,
    openAlertDialogDatePicker: MutableState<Boolean>,
    scope: CoroutineScope,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    eventVM: EventVM,
    onClickTask: () -> Unit,
    onClickTimer: () -> Unit
) {
    Scaffold(
        modifier = if (bottomSheetScaffoldState.bottomSheetState.isVisible){
            Modifier
                .fillMaxSize()
                .blur(4.dp)
        } else {
            Modifier.fillMaxSize()
        },
        topBar = { CalendarSettingsTopBar(openAlertDialogListOfCalendars) },
        floatingActionButton = { CalendarAddEventButton(scope, bottomSheetScaffoldState, eventVM) },
        bottomBar = { CalendarNavigationBar(onClickTask, onClickTimer)},
        containerColor = colorScheme.background
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            CalendarListEventsDate(eventVM, openAlertDialogDatePicker)
            CalendarListEvents(
                eventVM.eventsList,
                eventVM.tasksList,
                scope,
                bottomSheetScaffoldState,
                eventVM
            )
        }
    }
    CalendarBottomSheet(scope, bottomSheetScaffoldState, eventVM)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalendarSettingsTopBar(openAlertDialog: MutableState<Boolean>) {
    TopAppBar(
        title = {},
        colors =  TopAppBarDefaults.topAppBarColors(Color.Transparent),
        actions = {
            Button(
                onClick = { openAlertDialog.value = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Gray
                )
            ){
                Text(
                    text = "Выбор календаря",
                    fontSize = 14.sp
                )
            }
        }
    )
}

@Composable
fun CalendarListEventsDate(eventVM: EventVM, openDialogDatePicker: MutableState<Boolean>) {
    eventVM.getTasks()
    eventVM.getEvents()
    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("dd MMM yyyy", Locale("en"))
                .format(eventVM.selectedDate.value)
        }
    }
    Row(
        modifier = Modifier
            .padding(32.dp, 0.dp, 32.dp, 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(text = formattedDate)
        Spacer(modifier = Modifier.padding(8.dp))
        Button(onClick = {
            openDialogDatePicker.value = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = md_theme_light_onPrimary
        )) {
            Text(text = "Choose date")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarListEvents(
    events: MutableState<List<Event>>,
    tasks: MutableState<List<Task>>,
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    eventVM: EventVM
) {
    val is24Hour = DateFormat.is24HourFormat(LocalContext.current)
    val interactionSource = MutableInteractionSource()

    if (events.value.isNotEmpty() || tasks.value.isNotEmpty()) {
        LazyColumn {
            itemsIndexed(tasks.value)
            { _, item ->
                val checkedState = remember { mutableStateOf(item.check) }
                Row(
                    modifier = Modifier
                        .height(64.dp)
                        .fillMaxWidth()
                        .alpha(
                            if(checkedState.value) 0.4f else 1.0f
                        )
                        /*.clickable(
                            interactionSource = interactionSource,
                    indication = null,
                    onClick = {}
                        ) */
                    .padding(28.dp, 8.dp, 24.dp, 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .fillMaxHeight()
                            .padding(0.dp, 4.dp, 0.dp, 4.dp)
                            .background(
                                when(item.priority) {
                                1 -> priority_red
                                2 -> priority_yellow
                                3 -> priority_green
                                4 -> priority_blue
                                5 -> priority_purple
                                else -> Color.Transparent
                                }
                            )
                    )
                    Box(
                        modifier = Modifier.weight(if (is24Hour) 1f else 1.5f).padding(start = 4.dp, end = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (checkedState.value) {
                            Icon(
                                painter = painterResource(id = R.drawable.check_circle),
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable {
                                        checkedState.value = !checkedState.value
                                        eventVM.editStatus(item.id!!, checkedState.value)
                                    }
                                    .height(16.dp)
                                    .width(16.dp),
                                tint = Color.Gray
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.outline_circle),
                                contentDescription = null,
                                modifier = Modifier
                                    .clickable {
                                        checkedState.value = !checkedState.value
                                        eventVM.editStatus(item.id!!, checkedState.value)
                                    }
                                    .height(16.dp)
                                    .width(16.dp),
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(16.dp))
                            .fillMaxHeight()
                            .background(colorScheme.surface)
                            .weight(5f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            color = if(!item.check && (item.date ?: LocalDate.now().plusDays(1)) < LocalDate.now())
                                colorScheme.error
                                    else colorScheme.onBackground,
                            overflow = TextOverflow.Ellipsis,
                            text = item.title,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            itemsIndexed(events.value.sortedBy { it.start }.sortedBy { it.allDay == 0 }) { _, item ->
                Row(
                    modifier = Modifier
                        .height(64.dp)
                        .fillMaxWidth()
                        .alpha(
                            if((eventVM.selectedDate.value < LocalDate.now()) ||
                            (item.end.toLocalDate() == LocalDate.now()
                                    && item.end < LocalDateTime.now()
                                    && item.allDay == 0))
                                0.4f else 1.0f)
                        .padding(28.dp, 8.dp, 24.dp, 8.dp)
                        .clickable (
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = {
                                eventVM.pickedEventForBottomSheet(
                                    item.id,
                                    item.calendarId,
                                    item.start,
                                    item.end
                                )
                                scope.launch {
                                    scaffoldState.bottomSheetState.expand()
                                }
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .fillMaxHeight()
                            .padding(0.dp, 4.dp, 0.dp, 4.dp)
                            .background(Color(item.colorEvent
                                ?: item.colorCalendar!!)))
                    Box(
                        modifier = Modifier.weight(if (is24Hour) 1f else 1.5f).padding(start = 4.dp, end = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (item.allDay == 1) {
                                "All" + "\n" + "day"
                            } else {
                                item.start.format(DateTimeFormatter.ofPattern(if(is24Hour) "HH:mm" else "hh:mm a")) + "\n" + item.end.format(
                                    DateTimeFormatter.ofPattern(if(is24Hour) "HH:mm" else "hh:mm a"))
                            },
                            color = colorScheme.onBackground,
                            fontSize = 14.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(16.dp))
                            .fillMaxHeight()
                            .background(colorScheme.surface)
                            .weight(5f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            color = colorScheme.onBackground,
                            overflow = TextOverflow.Ellipsis,
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
                text = "Событий и задач на выбранный день нет",
                textAlign = TextAlign.Center
            )
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarAddEventButton(
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    eventVM: EventVM
) {
    FloatingActionButton(
        modifier = Modifier.padding(0.dp, 0.dp, 16.dp, 8.dp),
        onClick = {
            eventVM.newEventForBottomSheet()
            scope.launch {
                scaffoldState.bottomSheetState.expand()
            }
        },
        containerColor = colorScheme.tertiaryContainer,
        contentColor = colorScheme.onTertiaryContainer
    ) {
        Icon(
            Icons.Filled.Add,
            "Add event button"
        )
    }
}


@Composable
fun CalendarNavigationBar(onClickTask: () -> Unit, onClickTimer: () -> Unit) {
    var selectedItem by remember { mutableStateOf("calendar") }
    Divider(
        color = if(isSystemInDarkTheme()) colorScheme.surfaceVariant else Color.LightGray,
        thickness  = 1.dp,
        modifier = Modifier.padding(36.dp, 0.dp, 36.dp, 0.dp)
    )
    NavigationBar(
        modifier = Modifier.padding(48.dp, 0.dp, 48.dp, 16.dp),
        containerColor = Color.Transparent
    ) {
        NavigationBarItem(
            selected = selectedItem == "calendar",
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.date_range),
                    contentDescription = null
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = colorScheme.primary,
                unselectedIconColor = colorScheme.onBackground,
                indicatorColor = colorScheme.background
            ),
            onClick = {
                selectedItem = "calendar"
            }
        )
        NavigationBarItem(
            selected = selectedItem == "checklist",
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.checklist),
                    contentDescription = null
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = colorScheme.primary,
                unselectedIconColor = colorScheme.onBackground,
                indicatorColor = colorScheme.background
            ),
            onClick = {
                selectedItem = "checklist"
                onClickTask()
            }
        )
        NavigationBarItem(
            selected = selectedItem == "timer",
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.timer),
                    contentDescription = null
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = colorScheme.primary,
                unselectedIconColor = colorScheme.onBackground,
                indicatorColor = colorScheme.background
            ),
            onClick = {
                selectedItem = "timer"
                onClickTimer()
            }
        )
    }
}