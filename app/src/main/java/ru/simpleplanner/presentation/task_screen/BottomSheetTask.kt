package ru.simpleplanner.presentation.task_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import ru.simpleplanner.presentation.ui.theme.md_theme_light_onPrimary
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskBottomSheet(
    scope: CoroutineScope,
    scaffoldState: BottomSheetScaffoldState,
    taskVM: TaskVM
) {
    val openAlertDialogRepeatRule = remember { mutableStateOf(false) }
    val openAlertDialogNote = remember { mutableStateOf(false) }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        containerColor = colorScheme.background,
        sheetShadowElevation = 0.dp,
        sheetContent = { TaskBottomSheetContent(
            scope,
            scaffoldState,
            taskVM,
            openAlertDialogRepeatRule,
            openAlertDialogNote
        ) }
    ){}

    if(openAlertDialogRepeatRule.value) {
        TaskAlertDialogRepeatRule(openAlertDialogRepeatRule, taskVM)
    }

    if(openAlertDialogNote.value) {
        TaskAlertDialogNote(openAlertDialogNote, taskVM)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskBottomSheetContent(
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
        TaskBottomSheetTitle(taskVM)
        Spacer(modifier = Modifier.padding(8.dp))
        TaskBottomSheetDate(dateDialogState, taskVM, interactionSource)
        Spacer(modifier = Modifier.padding(8.dp))
        TaskBottomSheetRepeatRule(taskVM, openAlertDialogRepeatRule, interactionSource)
        Spacer(modifier = Modifier.padding(8.dp))
        TaskBottomSheetNote(openAlertDialogNote, interactionSource)
        Spacer(modifier = Modifier.padding(8.dp))
        TaskBottomSheetButtons(scope, scaffoldState, taskVM)
    }
    TaskBottomSheetDatePicker(dateDialogState, taskVM)
}

@Composable
fun TaskBottomSheetTitle(taskVM: TaskVM){
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
fun TaskBottomSheetWithoutDate(
    taskVM: TaskVM
){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(36.dp, 0.dp, 32.dp, 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            text = "Без даты",
            modifier = Modifier.weight(3f)
            ,
            textAlign = TextAlign.Start
        )
        Spacer(modifier = Modifier.padding(2.dp))
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End
        ) {
            val checked by remember {
                mutableStateOf(taskVM.withoutDateForBottomSheet)}
            Switch(
                checked = taskVM.withoutDateForBottomSheet.value,
                onCheckedChange = {
                    checked.value = it
                    taskVM.withoutDateForBottomSheet.value = it
                })
        }
    }
}
@Composable
fun TaskBottomSheetDate(
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
            textAlign = TextAlign.Start,
            color = if(taskVM.withoutDateForBottomSheet.value) Color.Gray
            else colorScheme.onBackground
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
                text = if(taskVM.withoutDateForBottomSheet.value) "Нет"
                            else formattedDate,
                textAlign = TextAlign.End,
                color = if(taskVM.withoutDateForBottomSheet.value) Color.Gray
                else colorScheme.onBackground
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = "Choose date",
                tint = if(taskVM.withoutDateForBottomSheet.value) Color.Gray
                else colorScheme.onBackground
            )
        }
    }
}

@Composable
fun TaskBottomSheetNote(
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
fun TaskBottomSheetRepeatRule(
    taskVM: TaskVM,
    openAlertDialogRepeatRule: MutableState<Boolean>,
    interactionSource: MutableInteractionSource
){
    Row(modifier = Modifier.fillMaxWidth()){
        Text(
            text = "Повтор",
            modifier = Modifier
                .weight(5f),
            textAlign = TextAlign.Start,
            color = if(taskVM.withoutDateForBottomSheet.value) Color.Gray else colorScheme.onBackground
        )
        Spacer(modifier = Modifier.padding(2.dp))
        Row( modifier = if(taskVM.withoutDateForBottomSheet.value) {
                Modifier.weight(6f)
            } else {
                Modifier
                    .weight(6f)
                    .clickable(
                        interactionSource = interactionSource,
                        onClick = { openAlertDialogRepeatRule.value = true },
                        indication = null
                    )
            },
            horizontalArrangement = Arrangement.End)
             {
            Text(
                text = if (taskVM.withoutDateForBottomSheet.value) "Нет" else
                    taskVM.repeatRuleForBottomSheet.value[0],
                textAlign = TextAlign.End,
                color = if(taskVM.withoutDateForBottomSheet.value) Color.Gray else colorScheme.onBackground
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = "Choose repeat",
                tint = if(taskVM.withoutDateForBottomSheet.value) Color.Gray else colorScheme.onBackground
            )
        }
    }
}

@Composable
fun TaskBottomSheetDatePicker(
    dateDialogState: MaterialDialogState,
    taskVM: TaskVM
){
    var pickedDateTemporal = if(taskVM.withoutDateForBottomSheet.value) LocalDate.now()
                                else taskVM.dateForBottomSheet.value
    MaterialDialog(
        dialogState = dateDialogState,
        backgroundColor = colorScheme.background,
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
                dateActiveBackgroundColor = colorScheme.primary,
                dateInactiveTextColor = colorScheme.onBackground,
                headerBackgroundColor = colorScheme.primary,
                headerTextColor = if(isSystemInDarkTheme()) colorScheme.onBackground else colorScheme.onPrimary,
                calendarHeaderTextColor = colorScheme.onBackground,
                dateActiveTextColor = if(isSystemInDarkTheme()) colorScheme.onBackground else colorScheme.onPrimary,
            )
        ) {
            pickedDateTemporal = it
        }
        TaskBottomSheetWithoutDate(taskVM = taskVM)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskBottomSheetButtons(
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
                    containerColor = colorScheme.errorContainer,
                    contentColor = colorScheme.onErrorContainer
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
                    containerColor = colorScheme.primary,
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
                    containerColor = colorScheme.primary,
                    contentColor = md_theme_light_onPrimary
                )
            ) {
                Text(text = "Добавить")
            }
        }
    }
}