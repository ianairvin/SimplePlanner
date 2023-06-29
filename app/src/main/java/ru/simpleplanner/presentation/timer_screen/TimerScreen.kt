package ru.simpleplanner.presentation.timer_screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.W700
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.simpleplanner.R
import ru.simpleplanner.presentation.ui.theme.light_primary
import ru.simpleplanner.presentation.ui.theme.md_theme_light_onPrimary


@Composable
fun TimerActivity(timerVM: TimerVM, onClickCalendar: () -> Unit, onClickTask: () -> Unit) {

    val openAlertDialogTimerSettings = remember { mutableStateOf(false) }
    if (openAlertDialogTimerSettings.value) {
        TimerSettingsAlertDialog(openAlertDialogTimerSettings, timerVM)
    }

    ScaffoldTimer(
        timerVM,
        onClickCalendar,
        onClickTask,
        openAlertDialogTimerSettings
    )
}

@Composable
private fun ScaffoldTimer(
    timerVM: TimerVM,
    onClickCalendar: () -> Unit,
    onClickTimer: () -> Unit,
    openAlertDialogTimerSettings: MutableState<Boolean>
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TimerSettingsTopBar(openAlertDialogTimerSettings) },
        bottomBar = { TimerNavigationBar(onClickCalendar, onClickTimer) },
        containerColor = colorScheme.background
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TimerScreenContent(timerVM)
        }
    }
}

@Composable
fun TimerScreenContent(timerVM: TimerVM){
    val interactionSource = MutableInteractionSource()
    TimerSwitchWorkOrRest(timerVM)
    TimerSwitchShortOrLongRest(timerVM, interactionSource)
    TimerNumbers(timerVM, interactionSource)
    TimerButtons(timerVM)
}

@Composable
fun TimerSwitchWorkOrRest(timerVM: TimerVM){
    val cornerRadius = 100
    Row(
        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 40.dp)
    ){
        OutlinedButton(
            shape = RoundedCornerShape(
                topStartPercent = cornerRadius,
                topEndPercent = 0,
                bottomStartPercent = cornerRadius,
                bottomEndPercent = 0
            ),
            colors = if(timerVM.isWorkScreen.value) { ButtonDefaults.buttonColors(
                containerColor = light_primary,
                contentColor = colorScheme.onBackground
            ) } else { ButtonDefaults.buttonColors(
                containerColor = colorScheme.background,
                contentColor = colorScheme.onBackground
            )} ,
            onClick = { timerVM.isWorkScreen.value = true }
        ) {
            Text("Работа")
        }
        OutlinedButton(
            shape = RoundedCornerShape(
                topStartPercent = 0,
                topEndPercent = cornerRadius,
                bottomStartPercent = 0,
                bottomEndPercent = cornerRadius
            ),
            colors = if(!timerVM.isWorkScreen.value) { ButtonDefaults.buttonColors(
                containerColor = light_primary,
                contentColor = colorScheme.onBackground
            ) } else { ButtonDefaults.buttonColors(
                containerColor = colorScheme.background,
                contentColor = colorScheme.onBackground
            )},
            onClick = { timerVM.isWorkScreen.value = false }
        ) {
            Text("Отдых")
        }
    }
}

@Composable
fun TimerSwitchShortOrLongRest(timerVM: TimerVM, interactionSource: MutableInteractionSource){
    Row (
        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 100.dp).height(36.dp)
    ){
        if (!timerVM.isWorkScreen.value) {
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowLeft,
                contentDescription = "Choose break",
                modifier = if(!timerVM.isTimerRunning.value){
                    Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { timerVM.isShortRest.value = !timerVM.isShortRest.value })
                } else {
                    Modifier
                },
                tint = if(timerVM.isTimerRunning.value) colorScheme.background else colorScheme.onBackground
            )
            Text(
                text = if (timerVM.isShortRest.value) "короткий перерыв" else "длинный перерыв"
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowRight,
                contentDescription = "Choose break",
                modifier = if(!timerVM.isTimerRunning.value){
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { timerVM.isShortRest.value = !timerVM.isShortRest.value })
                } else {
                    Modifier
                },
                tint = if(timerVM.isTimerRunning.value) colorScheme.background else colorScheme.onBackground
            )
        }
    }
}

@Composable
fun TimerNumbers(timerVM: TimerVM, interactionSource: MutableInteractionSource){
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ){
        Icon(
            imageVector = Icons.Outlined.KeyboardArrowLeft,
            contentDescription = "Choose time",
            modifier = if(!timerVM.isTimerRunning.value){
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { })
                    .height(36.dp).width(36.dp)
            } else {
                Modifier
            },
            tint = if(timerVM.isTimerRunning.value) colorScheme.background else colorScheme.onBackground
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Text(
            text = timerVM.timeTitleScreen.value,
            textAlign = TextAlign.Center,
            fontSize = 64.sp,
            fontWeight = W700
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Icon(
            imageVector = Icons.Outlined.KeyboardArrowRight,
            contentDescription = "Choose time",
            modifier = if(!timerVM.isTimerRunning.value){
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {  })
                    .height(36.dp).width(36.dp)
            } else {
                Modifier
            },
            tint = if(timerVM.isTimerRunning.value) colorScheme.background else colorScheme.onBackground
        )
    }
}

@Composable
fun TimerButtons(timerVM: TimerVM) {
    Row(
        modifier = Modifier.padding(0.dp, 120.dp, 0.dp, 140.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (timerVM.isTimerRunning.value) {
            Button(
                modifier = Modifier
                    .height(48.dp)
                    .width(132.dp),
                shape = RoundedCornerShape(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.errorContainer,
                    contentColor = colorScheme.onErrorContainer
                ),
                onClick = {
                    timerVM.resetTimer()
                }
            ) {
                Text(text = "Сброс")
            }
            Spacer(modifier = Modifier.padding(4.dp))
            Button(
                modifier = Modifier
                    .height(48.dp)
                    .width(132.dp),
                shape = RoundedCornerShape(36.dp),
                onClick = {
                    if (timerVM.isTimerOnPause.value) {
                        timerVM.startTimer()
                    } else {
                        timerVM.pauseTimer()
                    }
                },
                colors = if (timerVM.isTimerOnPause.value) {
                    ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary,
                        contentColor = md_theme_light_onPrimary
                    )
                } else {
                    ButtonDefaults.buttonColors(
                        containerColor = Color.Gray,
                        contentColor = md_theme_light_onPrimary
                    )
                }
            ) {
                Text(text = if (timerVM.isTimerOnPause.value) "Возобновить" else "Пауза")
            }
        } else {
            Button(
                modifier = Modifier
                    .height(48.dp)
                    .width(132.dp),
                shape = RoundedCornerShape(36.dp),
                onClick = {
                    timerVM.isTimerRunning.value = true
                    timerVM.startTimer(300000)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = md_theme_light_onPrimary
                )
            ) {
                Text(text = "Старт")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimerSettingsTopBar(openAlertDialogTimerSettings: MutableState<Boolean>) {
    TopAppBar(
        title = {},
        colors =  TopAppBarDefaults.topAppBarColors(Color.Transparent),
        actions = {
            IconButton(onClick = { openAlertDialogTimerSettings.value = true })
            {
                Icon(Icons.Outlined.Settings, contentDescription = "Settings")
            }
        }
    )
}

@Composable
fun TimerNavigationBar(onClickCalendar: () -> Unit, onClickTask: () -> Unit) {
    var selectedItem by remember { mutableStateOf("timer") }
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
            }
        )
    }
}