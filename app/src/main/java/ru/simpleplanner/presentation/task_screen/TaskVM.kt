package ru.simpleplanner.presentation.task_screen

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.simpleplanner.domain.entities.Event
import ru.simpleplanner.domain.use_case.event_uc.GetEventsUseCase
import ru.simpleplanner.domain.use_case.event_uc.GetTasksUseCase
import ru.simpleplanner.domain.use_case.task_uc.EditStatusTaskUseCase
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class TaskVM @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val editStatusTaskUseCase: EditStatusTaskUseCase
): ViewModel() {

    var tasksListToday = mutableStateOf(getTasksUseCase("Today"))
    var tasksListTomorrow = mutableStateOf(getTasksUseCase("Tomorrow"))
    var tasksListWeek = mutableStateOf(getTasksUseCase("Week"))
    var tasksListSomeDay = mutableStateOf(getTasksUseCase("SomeDay"))

    val titleForBottomSheet =  mutableStateOf("")
    val allDayForBottomSheet = mutableIntStateOf(0)
    val repeatRuleForBottomSheet =  mutableStateOf(arrayOf("Нет", ""))
    val noteForBottomSheet = mutableStateOf("")
    val dateForBottomSheet =  mutableStateOf(LocalDate.now())
 //   val idTaskForBottomSheet =  mutableStateOf("")

    val updaterBottomSheet = mutableStateOf(false)

    val repeatRule = arrayOf(
        arrayOf("Нет", ""),
        arrayOf("Каждый день", "DAILY/1"),
        arrayOf("Каждые два дня", "DAILY/2"),
        arrayOf("Каждую неделю", "WEEKLY/1"),
        arrayOf("Каждые две недели", "WEEKLY/2"),
        arrayOf("Каждый месяц", "MONTHLY/1")
    )

    fun editStatus(id: Int){
        editStatusTaskUseCase(id)
    }


    fun newTaskForBottomSheet(){
        updaterBottomSheet.value = false
    }

    fun pickedTaskForBottomSheet(id: String){
        updaterBottomSheet.value = true
    }

    fun saveOrUpdateTask() {

    }

    fun deleteTask(){

    }
}