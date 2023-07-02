package ru.simpleplanner.presentation.timer_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerAlertDialogClearNumberOfRepeat(
    openAlertDialog: MutableState<Boolean>,
    timerVM: TimerVM
){
    AlertDialog(onDismissRequest = {
        openAlertDialog.value = false
    }) {
        Surface(
            modifier = Modifier
                .width(360.dp)
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Вы действительно хотите сбросить количество пройденных интервалов?",
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    onClick = {
                        openAlertDialog.value = false
                        timerVM.numberOfRepeats.value = 0
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Да")
                }
            }
        }
    }
}