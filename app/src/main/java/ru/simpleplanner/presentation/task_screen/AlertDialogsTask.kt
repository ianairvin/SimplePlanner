package ru.simpleplanner.presentation.task_screen

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.simpleplanner.R
import ru.simpleplanner.presentation.ui.theme.priority_blue
import ru.simpleplanner.presentation.ui.theme.priority_green
import ru.simpleplanner.presentation.ui.theme.priority_purple
import ru.simpleplanner.presentation.ui.theme.priority_red
import ru.simpleplanner.presentation.ui.theme.priority_yellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskAlertDialogPriority(
    openAlertDialog: MutableState<Boolean>,
    taskVM: TaskVM
){
    val priorityTemporal = remember {
        mutableStateOf(taskVM.priorityForBottomSheet.value) }
    AlertDialog(onDismissRequest = {
        openAlertDialog.value = false
        priorityTemporal.value = taskVM.priorityForBottomSheet.value
    }) {
        Surface(
            modifier = Modifier
                .width(160.dp)
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
                    mutableStateOf(priorityTemporal.value)
                }
                taskVM.priority.forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.selectableGroup()) {
                        RadioButton(
                            selected = (item.contentEquals(selectedOption)),
                            onClick = {
                                onOptionSelected(item)
                                priorityTemporal.value = item
                            }
                        )
                        Text(
                            modifier = Modifier.padding(start = 2.dp, end = 8.dp),
                            text = item[0]
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.fill_circle),
                            contentDescription = null,
                            modifier = Modifier
                                .height(8.dp)
                                .width(8.dp),
                            tint = when(item[1]) {
                                "1" -> priority_red
                                "2" -> priority_yellow
                                "3" -> priority_green
                                "4" -> priority_blue
                                "5" -> priority_purple
                                else -> Color.Transparent
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = {
                        openAlertDialog.value = false
                        taskVM.priorityForBottomSheet.value = priorityTemporal.value
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
fun TaskAlertDialogNote(
    openAlertDialogNote: MutableState<Boolean>,
    taskVM: TaskVM
){
    val descriptionTemporal = remember {
        mutableStateOf(taskVM.noteForBottomSheet.value) }
    AlertDialog(onDismissRequest = {
        openAlertDialogNote.value = false
        descriptionTemporal.value = taskVM.noteForBottomSheet.value
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
                Text(text = "Заметки")
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
                        openAlertDialogNote.value = false
                        taskVM.noteForBottomSheet.value = descriptionTemporal.value
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Применить")
                }
            }
        }
    }
}