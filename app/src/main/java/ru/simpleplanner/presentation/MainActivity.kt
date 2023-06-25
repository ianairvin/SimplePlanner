package ru.simpleplanner.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import ru.simpleplanner.presentation.ui.theme.SimplePlannerTheme
import ru.simpleplanner.presentation.eventView.eventActivity

@AndroidEntryPoint
@OptIn(ExperimentalPermissionsApi::class)
@ExperimentalMaterial3Api
class MainActivity : ComponentActivity(
) {

    private val eventVM: EventVM by viewModels()
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {

        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        setContent {
            SimplePlannerTheme {
                uiController(isSystemInDarkTheme())
                eventActivity(eventVM)
            }
        }
    }

    @Composable
    fun uiController(darkTheme: Boolean){
        val systemUiController = rememberSystemUiController()
        SideEffect {
            systemUiController.setStatusBarColor(color = Color.Transparent)
            systemUiController.statusBarDarkContentEnabled = !darkTheme
            systemUiController.setNavigationBarColor(color = Color.Transparent)
        }
    }
}