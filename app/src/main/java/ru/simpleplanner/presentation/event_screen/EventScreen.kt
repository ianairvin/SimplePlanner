package ru.simpleplanner.presentation.event_screen

import ru.simpleplanner.R
import android.Manifest
import android.graphics.Color.parseColor
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.simpleplanner.domain.entities.Event
import ru.simpleplanner.presentation.ui.theme.md_theme_light_onPrimary
import ru.simpleplanner.presentation.ui.theme.neutral40
import ru.simpleplanner.presentation.ui.theme.neutral60
import ru.simpleplanner.presentation.ui.theme.surface
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalPermissionsApi
@Composable
fun eventActivity(eventVM: EventVM, onClickTask: () -> Unit, onClickTimer: () -> Unit) {
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
            initialValue = SheetValue.Hidden,
            confirmValueChange = {true}
        )
    )
    val scope = rememberCoroutineScope()

    if (permissionsState.allPermissionsGranted) {
        eventVM.permissionsGranted.value = true
        eventVM.getCalendars()
        eventVM.getEvents()
        scaffold(
            openAlertDialog,
            scope,
            bottomSheetScaffoldState,
            eventVM,
            onClickTask,
            onClickTimer
        )
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
        topBar = { settingsTopBar(openAlertDialog) },
        floatingActionButton = { addButton(scope, bottomSheetScaffoldState, eventVM) },
        bottomBar = { navigationBar(onClickTask, onClickTimer)},
        containerColor = colorScheme.background
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            pickDay(eventVM)
            listEvents(eventVM.eventsList, scope, bottomSheetScaffoldState, eventVM)
        }
    }
    bottomSheetEvent(scope, bottomSheetScaffoldState, eventVM)
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
            dateDialogState.show() },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = md_theme_light_onPrimary
        )) {
            Text(text = "Выбрать дату")
        }
    }
    var pickedDateTemporal = eventVM.selectedDate.value
    MaterialDialog(
        dialogState = dateDialogState,
        backgroundColor = colorScheme.background,
        shape = RoundedCornerShape(24.dp),
        buttons = {
            positiveButton(text = "ОК") {
                eventVM.selectedDate.value = pickedDateTemporal
            }
            negativeButton(text = "Отмена")
        },
    ) {
        datepicker(
            initialDate = eventVM.selectedDate.value,
            title = "",
            locale = Locale("ru"),
            colors = DatePickerDefaults.colors(
                dateActiveBackgroundColor = colorScheme.primary,
                dateInactiveTextColor = colorScheme.onBackground,
                headerBackgroundColor = colorScheme.primary,
                headerTextColor = if(isSystemInDarkTheme()) colorScheme.onBackground else colorScheme.onPrimary,
                calendarHeaderTextColor = colorScheme.onBackground,
                dateActiveTextColor = if(isSystemInDarkTheme()) colorScheme.onBackground else colorScheme.onPrimary
            )

        ) {
            pickedDateTemporal = it
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
            itemsIndexed(events.value.sortedBy { it.start }.sortedBy { it.allDay == 0 }) { _, item ->
                Row(
                    modifier = Modifier
                        .height(64.dp)
                        .fillMaxWidth()
                        .padding(24.dp, 8.dp, 24.dp, 8.dp)
                        .clickable {
                            eventVM.pickedEventForBottomSheet(
                                item.id,
                                item.calendarId,
                                item.start,
                                item.end
                            )
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (item.allDay == 1) {
                            "Весь" + "\n" + "день"
                        } else {
                            item.start.format(DateTimeFormatter.ofPattern("HH:mm")) + "\n" + item.end.format(DateTimeFormatter.ofPattern("HH:mm"))
                        },
                        color = if(
                            (item.end.toLocalDate() < LocalDate.now()) ||
                            (item.end.toLocalDate() == LocalDate.now()
                                    && item.end < LocalDateTime.now()
                                    && item.allDay == 0)
                        ) {
                            if (isSystemInDarkTheme()) neutral40 else neutral60
                        } else {
                            colorScheme.onBackground },
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(16.dp))
                            .fillMaxHeight()
                            .background(
                                if (isSystemInDarkTheme()) {
                                    Color(
                                        ColorUtils.blendARGB(
                                            parseColor(
                                                "#96" +
                                                        (Integer
                                                            .toHexString(
                                                                item.colorEvent
                                                                    ?: item.colorCalendar!!
                                                            )
                                                            .drop(2))
                                            ),
                                            Color.White.toArgb(), 0.3f
                                        )
                                    )
                                } else {
                                    Color(
                                        ColorUtils.blendARGB(
                                            parseColor(
                                                "#96" +
                                                        (Integer
                                                            .toHexString(
                                                                item.colorEvent
                                                                    ?: item.colorCalendar!!
                                                            )
                                                            .drop(2))
                                            ), Color.White.toArgb(), 0.1f
                                        )
                                    )
                                }
                            )
                            .weight(6f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            color =if(
                                (item.end.toLocalDate() < LocalDate.now()) ||
                                (item.end.toLocalDate() == LocalDate.now()
                                        && item.end < LocalDateTime.now()
                                        && item.allDay == 0)
                            ) {
                                if (isSystemInDarkTheme()) neutral40 else neutral60
                            } else {
                                colorScheme.onBackground },
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
                text = "Событий на выбранный день в данных календарях нет",
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
fun navigationBar(onClickTask: () -> Unit, onClickTimer: () -> Unit) {
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