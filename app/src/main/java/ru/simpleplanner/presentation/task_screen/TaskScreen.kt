package ru.simpleplanner.presentation.task_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.BottomSheetScaffoldState
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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight.Companion.W600
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.simpleplanner.R
import ru.simpleplanner.domain.entities.Task
import ru.simpleplanner.presentation.ui.theme.priority_blue
import ru.simpleplanner.presentation.ui.theme.priority_green
import ru.simpleplanner.presentation.ui.theme.priority_purple
import ru.simpleplanner.presentation.ui.theme.priority_red
import ru.simpleplanner.presentation.ui.theme.priority_yellow
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalPermissionsApi
@Composable
fun TaskActivity(taskVM: TaskVM, onClickCalendar: () -> Unit, onClickTimer: () -> Unit) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = SheetState(
            skipPartiallyExpanded = true,
            initialValue = SheetValue.Hidden
        )
    )
    val scope = rememberCoroutineScope()

    TaskScaffold(
        scope,
        bottomSheetScaffoldState,
        taskVM,
        onClickCalendar,
        onClickTimer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScaffold(
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
        floatingActionButton = { AddTaskButton(scope, bottomSheetScaffoldState, taskVM) },
        bottomBar = { TaskNavigationBar(onClickCalendar, onClickTimer) },
        containerColor = colorScheme.background
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            TaskScreenContent(
                taskVM,
                taskVM.tasksListToday.collectAsState(initial = emptyList()),
                taskVM.tasksListTomorrow.collectAsState(initial = emptyList()),
                taskVM.tasksListWeek.collectAsState(initial = emptyList()),
                taskVM.tasksListSomeDay.collectAsState(initial = emptyList()),
                taskVM.tasksListDone.collectAsState(initial = emptyList()),
                scope,
                bottomSheetScaffoldState
            )
        }
    }
    TaskBottomSheet(scope, bottomSheetScaffoldState, taskVM)
}

@SuppressLint("UnrememberedMutableInteractionSource")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreenContent(
    taskVM: TaskVM,
    tasksListToday: State<List<Task>>,
    tasksListTomorrow: State<List<Task>>,
    tasksListWeek: State<List<Task>>,
    tasksListSomeDay: State<List<Task>>,
    tasksListDone: State<List<Task>>,
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState
) {
    val todayListOpen = remember { mutableStateOf(false) }
    val tomorrowListOpen = remember { mutableStateOf(false) }
    val weekListOpen = remember { mutableStateOf(false) }
    val someDayListOpen = remember { mutableStateOf(false) }
    val doneListOpen = remember { mutableStateOf(false) }

    val interactionSource = MutableInteractionSource()

    LazyColumn(
        modifier = Modifier
            .padding(0.dp, 32.dp, 0.dp, 32.dp)
    ) {
        item {
            TitleSectionTaskScreen(todayListOpen, "Сегодня", interactionSource)
        }
        if (todayListOpen.value && tasksListToday.value.isNotEmpty()) {
            itemsIndexed(tasksListToday.value.sortedBy { it.makeDateTime })
            { _, item -> ListTasks(item, taskVM, scope, scaffoldState, interactionSource) }
        } else if (todayListOpen.value){
            item { NoTasksMessage() }
        }

        item {
            TitleSectionTaskScreen(tomorrowListOpen, "Завтра", interactionSource)
        }
        if (tomorrowListOpen.value && tasksListTomorrow.value.isNotEmpty()) {
            itemsIndexed(tasksListTomorrow.value.sortedBy { it.makeDateTime })
            { _, item -> ListTasks(item, taskVM, scope, scaffoldState, interactionSource) }
        } else if (tomorrowListOpen.value){
            item { NoTasksMessage() }
        }

        item {
            TitleSectionTaskScreen(weekListOpen, "На неделе", interactionSource)
        }
        if (weekListOpen.value && tasksListWeek.value.isNotEmpty()) {
            itemsIndexed(tasksListWeek.value.sortedBy { it.makeDateTime })
            { _, item -> ListTasks(item, taskVM, scope, scaffoldState, interactionSource) }
        } else if (weekListOpen.value){
            item { NoTasksMessage() }
        }

        item {
            TitleSectionTaskScreen(someDayListOpen, "Когда-нибудь", interactionSource)
        }
        if (someDayListOpen.value && tasksListSomeDay.value.isNotEmpty()) {
            itemsIndexed(tasksListSomeDay.value.sortedBy { it.makeDateTime })
            { _, item -> ListTasks(item, taskVM, scope, scaffoldState, interactionSource) }
        } else if (someDayListOpen.value){
            item { NoTasksMessage() }
        }

        item {
            TitleSectionTaskScreen(doneListOpen, "Выполненные", interactionSource)
        }
        if (doneListOpen.value && tasksListDone.value.isNotEmpty()) {
            itemsIndexed(tasksListDone.value.sortedBy { it.makeDateTime })
            { _, item -> ListTasks(item, taskVM, scope, scaffoldState, interactionSource) }
        } else if (doneListOpen.value) {
            item { NoTasksMessage() }
        }
    }
}

@Composable
fun NoTasksMessage(){
    Text(
        modifier = Modifier
            .padding(36.dp, 8.dp, 36.dp, 8.dp)
            .fillMaxWidth(),
        text = "Задач нет",
        textAlign = TextAlign.Center,
        color = Color.Gray
    )
}

@Composable
fun TitleSectionTaskScreen(
    listOpen: MutableState<Boolean>,
    text: String,
    interactionSource: MutableInteractionSource
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(40.dp, 8.dp, 24.dp, 0.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier
                .weight(6f),
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
                .weight(1f)
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
fun ListTasks(
    item: Task,
    taskVM: TaskVM,
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    interactionSource: MutableInteractionSource
) {
    Row(
        modifier = Modifier
            .padding(52.dp, 8.dp, 36.dp, 8.dp)
            .height(24.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val checkedState = remember { mutableStateOf(item.check) }
        if (checkedState.value) {
            Icon(
                painter = painterResource(id = R.drawable.check_circle),
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        checkedState.value = !checkedState.value
                        taskVM.editStatus(item.id!!, checkedState.value)
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
                        taskVM.editStatus(item.id!!, checkedState.value)
                    }
                    .height(16.dp)
                    .width(16.dp)
            )
        }

        Spacer(modifier = Modifier.padding(4.dp))

        Text(
            text = item.title,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 0.dp)
                .clickable(
                    interactionSource = interactionSource,
                    onClick = {
                        taskVM.pickedTaskForBottomSheet(item.id!!)
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    },
                    indication = null
                ),
            fontWeight = W600,
            color = if (item.check) Color.Gray
            else if ((item.date ?: LocalDate.now()
                    .plusDays(1)) < LocalDate.now()
            ) colorScheme.error
            else colorScheme.onBackground
        )
        Spacer(modifier = Modifier.padding(end = 4.dp))
        Icon(
            painter = painterResource(id = R.drawable.fill_circle),
            contentDescription = null,
            modifier = Modifier
                .height(8.dp)
                .width(8.dp),
            tint = when(item.priority) {
                1 -> priority_red
                2 -> priority_yellow
                3 -> priority_green
                4 -> priority_blue
                5 -> priority_purple
                else -> Color.Transparent
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskButton(
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
fun TaskNavigationBar(onClickCalendar: () -> Unit, onClickTimer: () -> Unit) {
    var selectedItem by remember { mutableStateOf("checklist") }
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
