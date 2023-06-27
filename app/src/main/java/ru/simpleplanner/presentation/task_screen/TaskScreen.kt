package ru.simpleplanner.presentation.task_screen

import android.Manifest
import android.icu.text.CaseMap.Title
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.simpleplanner.R
import ru.simpleplanner.domain.entities.Task
import ru.simpleplanner.presentation.event_screen.descriptionForEvent


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalPermissionsApi
@Composable
fun taskActivity(taskVM: TaskVM, onClickCalendar: () -> Unit, onClickTimer: () -> Unit) {
    var bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            skipPartiallyExpanded = true,
            initialValue = SheetValue.Hidden
        )
    )
    val scope = rememberCoroutineScope()

    scaffold(
        scope,
        bottomSheetScaffoldState,
        taskVM,
        onClickCalendar,
        onClickTimer
        )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
private fun scaffold(
    scope: CoroutineScope,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    taskVM: TaskVM,
    onClickCalendar: () -> Unit,
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
        floatingActionButton = { addButton(scope, bottomSheetScaffoldState, taskVM) },
        bottomBar = { navigationBar(onClickCalendar, onClickTimer) },
        containerColor = colorScheme.background
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            taskScreenContent(
                taskVM,
                taskVM.tasksListToday,
                taskVM.tasksListTomorrow,
                taskVM.tasksListWeek,
                taskVM.tasksListSomeDay,
                scope,
                bottomSheetScaffoldState
            )
        }
    }
    bottomSheetTask(scope, bottomSheetScaffoldState, taskVM)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun taskScreenContent(
    taskVM: TaskVM,
    tasksListToday: MutableState<List<Task>>,
    tasksListTomorrow: MutableState<List<Task>>,
    tasksListWeek: MutableState<List<Task>>,
    tasksListSomeDay: MutableState<List<Task>>,
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState
) {
    val todayListOpen = remember { mutableStateOf(false) }
    val tomorrowListOpen = remember { mutableStateOf(false) }
    val weekListOpen = remember { mutableStateOf(false) }
    val someDayListOpen = remember { mutableStateOf(false) }

    val interactionSource = MutableInteractionSource()

    LazyColumn(
        modifier = Modifier
            .padding(0.dp, 32.dp, 0.dp, 32.dp)
    ) {
        item {
            titleSection(todayListOpen, "Сегодня", interactionSource)
        }
        if (todayListOpen.value && tasksListToday.value.isNotEmpty()) {
            itemsIndexed(tasksListToday.value.sortedBy { it.makeDateTime })
            { _, item -> listTasks(item, taskVM, scope, scaffoldState) }
        } else if (todayListOpen.value){
            item { noTasksMessage() }
        }

        item {
            titleSection(tomorrowListOpen, "Завтра", interactionSource)
        }
        if (tomorrowListOpen.value && tasksListTomorrow.value.isNotEmpty()) {
            itemsIndexed(tasksListTomorrow.value.sortedBy { it.makeDateTime })
            { _, item -> listTasks(item, taskVM, scope, scaffoldState) }
        } else if (tomorrowListOpen.value){
            item { noTasksMessage() }
        }

        item {
            titleSection(weekListOpen, "На неделе", interactionSource)
        }
        if (weekListOpen.value && tasksListWeek.value.isNotEmpty()) {
            itemsIndexed(tasksListWeek.value.sortedBy { it.makeDateTime })
            { _, item -> listTasks(item, taskVM, scope, scaffoldState) }
        } else if (weekListOpen.value){
            item { noTasksMessage() }
        }

        item {
            titleSection(someDayListOpen, "Когда-нибудь", interactionSource)
        }
        if (someDayListOpen.value && tasksListSomeDay.value.isNotEmpty()) {
            itemsIndexed(tasksListSomeDay.value.sortedBy { it.makeDateTime })
            { _, item -> listTasks(item, taskVM, scope, scaffoldState) }
        } else if (someDayListOpen.value){
            item { noTasksMessage() }
        }
    }
}

@Composable
fun noTasksMessage(){
    Text(
        modifier = Modifier.padding(36.dp, 0.dp, 36.dp, 0.dp).fillMaxWidth(),
        text = "Задач нет",
        textAlign = TextAlign.Center,
        color = Color.Gray
    )
}

@Composable
fun titleSection(
    listOpen: MutableState<Boolean>,
    text: String,
    interactionSource: MutableInteractionSource
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(40.dp, 8.dp, 0.dp, 8.dp)
                .weight(7f),
            textAlign = TextAlign.Start,
            fontSize = 20.sp,
            fontWeight = FontWeight.W700,
            color = if(listOpen.value) colorScheme.primary else colorScheme.onBackground
        )
        Icon(
            imageVector = if (listOpen.value) {
                Icons.Outlined.KeyboardArrowDown
            } else {
                Icons.Outlined.KeyboardArrowRight
            },
            contentDescription = "",
            modifier = Modifier
                .weight(3f)
                .clickable(
                    interactionSource = interactionSource,
                    onClick = { listOpen.value = !listOpen.value },
                    indication = null
                )
                .padding(0.dp, 8.dp, 0.dp, 0.dp),
            tint = if(listOpen.value) colorScheme.primary else colorScheme.onBackground
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun listTasks(
    item: Task,
    taskVM: TaskVM,
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState
) {
    Row(
        modifier = Modifier.padding(36.dp, 0.dp, 36.dp, 0.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val checkedState = remember { mutableStateOf(item.check) }
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = {
                checkedState.value = !checkedState.value
                taskVM.editStatus(item.id)
            }
        )
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
            text = item.title,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            modifier =  Modifier.clickable{
                scope.launch{
                    scaffoldState.bottomSheetState.expand()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun addButton(
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    taskVM: TaskVM
) {
    FloatingActionButton(
        modifier = Modifier.padding(0.dp, 0.dp, 16.dp, 8.dp),
        onClick = {
            taskVM.newTaskForBottomSheet()
            scope.launch {
                scaffoldState.bottomSheetState.expand()
            }
        },
        containerColor = colorScheme.tertiaryContainer,
        contentColor = colorScheme.onTertiaryContainer
    ) {
        Icon(
            Icons.Filled.Add,
            "Add task button"
        )
    }
}

@Composable
fun navigationBar(onClickCalendar: () -> Unit, onClickTimer: () -> Unit) {
    var selectedItem by remember { mutableStateOf("checklist") }
    Divider(
        color = colorScheme.surfaceVariant,
        thickness  = 1.dp,
        modifier = Modifier.padding(32.dp, 0.dp, 32.dp, 0.dp)
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
                onClickCalendar()
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
