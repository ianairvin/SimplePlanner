package ru.simpleplanner.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import ru.simpleplanner.presentation.event_screen.EventVM
import ru.simpleplanner.presentation.ui.theme.SimplePlannerTheme
import ru.simpleplanner.presentation.event_screen.EventActivity
import ru.simpleplanner.presentation.task_screen.TaskVM
import ru.simpleplanner.presentation.task_screen.TaskActivity
import ru.simpleplanner.presentation.timer_screen.TimerVM
import ru.simpleplanner.presentation.timer_screen.TimerActivity

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
                Box(modifier = Modifier.background(colorScheme.background)) {
                    UiController(isSystemInDarkTheme())
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
                }
            }
        }
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
}