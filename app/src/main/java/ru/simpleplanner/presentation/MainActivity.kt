package ru.simpleplanner.presentation

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import ru.simpleplanner.presentation.event_screen.EventVM
import ru.simpleplanner.presentation.ui.theme.SimplePlannerTheme
import ru.simpleplanner.presentation.event_screen.EventActivity
import ru.simpleplanner.presentation.task_screen.TaskVM
import ru.simpleplanner.presentation.task_screen.TaskActivity
import ru.simpleplanner.presentation.timer_screen.TimerVM
import ru.simpleplanner.presentation.timer_screen.TimerActivity
import ru.simpleplanner.presentation.ui.theme.md_theme_dark_onBackground

@AndroidEntryPoint
@OptIn(ExperimentalPermissionsApi::class)
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity(
) {

    private val eventVM: EventVM by viewModels()
    private val taskVM: TaskVM by viewModels()
    private val timerVM: TimerVM by viewModels()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {

        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            SimplePlannerTheme {
                val permissionsState = rememberMultiplePermissionsState(
                    permissions = listOf(
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR,
                        Manifest.permission.VIBRATE
                    )
                )
                Box(modifier = Modifier.background(colorScheme.background)) {
                    UiController(isSystemInDarkTheme())
                    if (permissionsState.allPermissionsGranted) {
                        NavHost(navController = navController, startDestination = "event_screen") {

                            composable("event_screen") {
                                EventActivity(eventVM = eventVM,
                                    onClickTask = {
                                        navController.navigate("task_screen") {
                                            popUpTo(navController.graph.id) {
                                                inclusive = true
                                            }
                                        }
                                    },
                                    onClickTimer = {
                                        navController.navigate("timer_screen") {
                                            popUpTo(navController.graph.id) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                )

                            }

                            composable("task_screen") {
                                TaskActivity(taskVM,
                                    onClickCalendar = {
                                        navController.navigate("event_screen") {
                                            popUpTo(navController.graph.id) {
                                                inclusive = true
                                            }
                                        }
                                    },
                                    onClickTimer = {
                                        navController.navigate("timer_screen") {
                                            popUpTo(navController.graph.id) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                )
                            }

                            composable("timer_screen") {
                                TimerActivity(timerVM,
                                    onClickCalendar = {
                                        navController.navigate("event_screen") {
                                            popUpTo(navController.graph.id) {
                                                inclusive = true
                                            }
                                        }
                                    },
                                    onClickTask = {
                                        navController.navigate("task_screen") {
                                            popUpTo(navController.graph.id) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    } else {
                        GetPermissions(permissionsState)
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        timerVM.saveTime()
        eventVM.savePickedCalendars()
    }
    @Composable
    fun UiController(darkTheme: Boolean){
        val systemUiController = rememberSystemUiController()
        SideEffect {
            systemUiController.setStatusBarColor(color = Color.Transparent)
            systemUiController.statusBarDarkContentEnabled = !darkTheme
            systemUiController.setNavigationBarColor(color = Color.Transparent)
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    private fun GetPermissions(permissionsState: MultiplePermissionsState) {
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
                    Button(
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = md_theme_dark_onBackground
                        ),
                        onClick = { permissionsState.launchMultiplePermissionRequest() }
                    ) {
                        Text("Дать разрешение")
                    }
                }
            }
        )
    }
}