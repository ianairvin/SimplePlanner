package ru.simpleplanner.presentation.task_screen

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.CoroutineScope
import ru.simpleplanner.R


@OptIn(ExperimentalMaterial3Api::class)
@ExperimentalPermissionsApi
@Composable
fun taskActivity(taskVM: TaskVM) {
   // scaffold(openAlertDialog = taskVM)
}
/*
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
private fun scaffold(
    openAlertDialog: MutableState<Boolean>,
    scope: CoroutineScope,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    taskVM: TaskVM
) {
    Scaffold(
        modifier = if (bottomSheetScaffoldState.bottomSheetState.isVisible){
            Modifier
                .fillMaxSize()
                .blur(4.dp)
        } else {
            Modifier.fillMaxSize()
        },
        topBar = { settingsTopBar(openAlertDialog) },
        floatingActionButton = { addButton(scope, bottomSheetScaffoldState, eventVM) },
        bottomBar = { navigationBar() },
        containerColor = colorScheme.background
    ) { contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            pickDay(eventVM)
            listEvents(eventVM.eventsList, scope, bottomSheetScaffoldState, eventVM)
        }
    }
    bottomSheet(scope, bottomSheetScaffoldState, eventVM)
}

@Composable
fun navigationBar() {
    var selectedItem by remember { mutableStateOf("calendar") }
    Divider(
        color = colorScheme.surfaceVariant,
        thickness  = 1.dp,
        modifier = Modifier.padding(32.dp, 8.dp, 32.dp, 8.dp)
    )
    NavigationBar(
        modifier = Modifier.padding(32.dp, 0.dp, 32.dp, 8.dp),
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
            onClick = { selectedItem = "calendar" }
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
            onClick = { selectedItem = "checklist" }
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
            onClick = { selectedItem = "timer" }
        )
    }

}
 */