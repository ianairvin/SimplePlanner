package ru.simpleplanner.presentation.task_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.simpleplanner.presentation.event_screen.EventVM
import ru.simpleplanner.presentation.event_screen.button
import ru.simpleplanner.presentation.event_screen.calendarForEvent
import ru.simpleplanner.presentation.event_screen.dateAndTimeEvent
import ru.simpleplanner.presentation.event_screen.descriptionForEvent
import ru.simpleplanner.presentation.event_screen.locationForEvent
import ru.simpleplanner.presentation.event_screen.pickAllDay
import ru.simpleplanner.presentation.event_screen.pickCalendar
import ru.simpleplanner.presentation.event_screen.pickDate
import ru.simpleplanner.presentation.event_screen.pickDescription
import ru.simpleplanner.presentation.event_screen.pickEndTime
import ru.simpleplanner.presentation.event_screen.pickLocation
import ru.simpleplanner.presentation.event_screen.pickRepeatRule
import ru.simpleplanner.presentation.event_screen.pickStartTime
import ru.simpleplanner.presentation.event_screen.repeatRuleForEvent
import ru.simpleplanner.presentation.event_screen.titleEvent
import ru.simpleplanner.presentation.ui.theme.md_theme_light_onPrimary
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun bottomSheetTask(
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    taskVM: TaskVM
) {
    val openAlertDialogRepeatRule = remember { mutableStateOf(false) }
    val openAlertDialogNote = remember { mutableStateOf(false) }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        containerColor = MaterialTheme.colorScheme.background,
        sheetShadowElevation = 0.dp,
        sheetContent = { bottomSheetContentTask(
            scope,
            scaffoldState,
            taskVM,
            openAlertDialogRepeatRule,
            openAlertDialogNote
        ) }
    ){}

    if(openAlertDialogRepeatRule.value) {
        repeatRuleForTask(openAlertDialogRepeatRule, taskVM)
    }

    if(openAlertDialogNote.value) {
        noteForTask(openAlertDialogNote, taskVM)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun bottomSheetContentTask(
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    taskVM: TaskVM,
    openAlertDialogRepeatRule: MutableState<Boolean>,
    openAlertDialogNote: MutableState<Boolean>
) {
    val interactionSource = MutableInteractionSource()

    val dateDialogState = rememberMaterialDialogState()

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(32.dp, 0.dp, 32.dp, 0.dp)
    ){
        titleTask(taskVM)
        Spacer(modifier = Modifier.padding(8.dp))
        dateTask(dateDialogState, taskVM, interactionSource)
        Spacer(modifier = Modifier.padding(8.dp))
        pickRepeatRule(taskVM, openAlertDialogRepeatRule, interactionSource)
        //Spacer(modifier = Modifier.padding(8.dp))
       // pickColor()
        Spacer(modifier = Modifier.padding(8.dp))
        pickNote(openAlertDialogNote, interactionSource)
        Spacer(modifier = Modifier.padding(8.dp))
        button(scope, scaffoldState, taskVM)
    }
    pickDate(dateDialogState, taskVM)
}

@Composable
fun titleTask(taskVM: TaskVM){
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ){
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = taskVM.titleForBottomSheet.value,
            onValueChange = { taskVM.titleForBottomSheet.value = it },
            label = { Text(text = "Введите название") },
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )
    }
}

@Composable
fun dateTask(
    dateDialogState: MaterialDialogState,
    taskVM: TaskVM,
    interactionSource: MutableInteractionSource
){
    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("dd LLL u", Locale("ru"))
                .format(taskVM.dateForBottomSheet.value)
        }
    }
    Row(modifier = Modifier.fillMaxWidth()){
        Text(
            text = "День",
            modifier = Modifier
                .weight(5f),
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.padding(2.dp))
        Row( modifier = Modifier
            .weight(6f)
            .clickable(
                interactionSource = interactionSource,
                onClick = { dateDialogState.show() },
                indication = null
            ),
            horizontalArrangement = Arrangement.End) {
            Text(
                text = "day",
                textAlign = TextAlign.End
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = "Choose date"
            )
        }
    }
}

@Composable
fun pickNote(
    openAlertDialogNote: MutableState<Boolean>,
    interactionSource: MutableInteractionSource
){
    Row(modifier = Modifier.fillMaxWidth()){
        Text(
            text = "Заметки",
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(5f)
        )
        Row(modifier = Modifier
            .weight(6f)
            .clickable(
                interactionSource = interactionSource,
                onClick = { openAlertDialogNote.value = true },
                indication = null
            ),
            horizontalArrangement = Arrangement.End
        ){
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = "Write note"
            )
        }
    }
}

@Composable
fun pickRepeatRule(
    taskVM: TaskVM,
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
            .weight(6f)
            .clickable(
                interactionSource = interactionSource,
                onClick = { openAlertDialogRepeatRule.value = true },
                indication = null
            ),
            horizontalArrangement = Arrangement.End) {
            Text(
                text = taskVM.repeatRuleForBottomSheet.value[0],
                textAlign = TextAlign.End
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = "Choose repeat"
            )
        }
    }
}

@Composable
fun pickDate(
    dateDialogState: MaterialDialogState,
    taskVM: TaskVM
){
    var pickedDateTemporal = taskVM.dateForBottomSheet.value
    MaterialDialog(
        dialogState = dateDialogState,
        backgroundColor = MaterialTheme.colorScheme.background,
        buttons = {
            positiveButton(text = "ОК") {
                taskVM.dateForBottomSheet.value = pickedDateTemporal
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
                dateActiveBackgroundColor = MaterialTheme.colorScheme.primary,
                dateInactiveTextColor = MaterialTheme.colorScheme.onBackground,
                headerBackgroundColor = MaterialTheme.colorScheme.primary,
                headerTextColor = if(isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onPrimary,
                calendarHeaderTextColor = MaterialTheme.colorScheme.onBackground,
                dateActiveTextColor = if(isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onPrimary,
            )
        ) {
            pickedDateTemporal = it
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun button(
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    taskVM: TaskVM
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp, 16.dp, 16.dp, 56.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        if(taskVM.updaterBottomSheet.value) {
            Button(
                modifier = Modifier
                    .height(48.dp)
                    .weight(1f),
                shape = RoundedCornerShape(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                onClick = {
                    taskVM.deleteTask()
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
                    taskVM.saveOrUpdateTask()
                    scope.launch {
                        scaffoldState.bottomSheetState.hide()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
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
                    taskVM.saveOrUpdateTask()
                    scope.launch {
                        scaffoldState.bottomSheetState.hide()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = md_theme_light_onPrimary
                )
            ) {
                Text(text = "Добавить")
            }
        }
    }
}