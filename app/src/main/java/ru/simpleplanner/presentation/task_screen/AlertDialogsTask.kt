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
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskAlertDialogRepeatRule(
    openAlertDialog: MutableState<Boolean>,
    taskVM: TaskVM
){
    val repeatRuleTemporal = remember {
        mutableStateOf(taskVM.repeatRuleForBottomSheet.value) }
    AlertDialog(onDismissRequest = {
        openAlertDialog.value = false
        repeatRuleTemporal.value = taskVM.repeatRuleForBottomSheet.value
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
                val (selectedOption, onOptionSelected) = remember {
                    mutableStateOf(repeatRuleTemporal.value)
                }
                taskVM.repeatRule.forEach { item ->
                    Row(verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.selectableGroup()) {
                        RadioButton(
                            selected = (item.contentEquals(selectedOption)),
                            onClick = {
                                onOptionSelected(item)
                                repeatRuleTemporal.value = item
                            }
                        )
                        Text(
                            modifier = Modifier.padding(start = 2.dp),
                            text = item[0]
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = {
                        openAlertDialog.value = false
                        taskVM.repeatRuleForBottomSheet.value = repeatRuleTemporal.value
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