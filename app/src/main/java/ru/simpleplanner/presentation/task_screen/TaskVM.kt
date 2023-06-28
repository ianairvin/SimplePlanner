package ru.simpleplanner.presentation.task_screen

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.simpleplanner.domain.entities.Task
import ru.simpleplanner.domain.use_case.task_uc.DeleteTaskUseCase
import ru.simpleplanner.domain.use_case.task_uc.EditStatusTaskUseCase
import ru.simpleplanner.domain.use_case.task_uc.GetOneTaskUseCase
import ru.simpleplanner.domain.use_case.task_uc.GetTasksUseCase
import ru.simpleplanner.domain.use_case.task_uc.InsertTaskUseCase
import ru.simpleplanner.domain.use_case.task_uc.UpdateTaskUseCase
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TaskVM @Inject constructor(
    private val insertTaskUseCase: InsertTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getOneTaskUseCase: GetOneTaskUseCase,
    private val getTasksUseCase: GetTasksUseCase,
    private val editStatusTaskUseCase: EditStatusTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
): ViewModel() {

    var tasksListToday = getTasksUseCase("Today")
    var tasksListTomorrow = getTasksUseCase("Tomorrow")
    var tasksListWeek = getTasksUseCase("Week")
    var tasksListSomeDay = getTasksUseCase("SomeDay")

    val titleForBottomSheet =  mutableStateOf("")
    val repeatRuleForBottomSheet =  mutableStateOf(arrayOf("Нет", ""))
    val noteForBottomSheet = mutableStateOf("")
    val dateForBottomSheet =  mutableStateOf(LocalDate.now())
    val idForBottomSheet =  mutableIntStateOf(0)
    val checkForBottomSheet =  mutableStateOf(false)
    val withoutDateForBottomSheet =  mutableStateOf(false)

    val updaterBottomSheet = mutableStateOf(false)

    val repeatRule = arrayOf(
        arrayOf("Нет", ""),
        arrayOf("Каждый день", "DAILY"),
        arrayOf("Каждую неделю", "WEEKLY")
    )

    fun newTaskForBottomSheet(){
        updaterBottomSheet.value = false

        titleForBottomSheet.value =  ""
        repeatRuleForBottomSheet.value =  arrayOf("Нет", "")
        noteForBottomSheet.value = ""
        dateForBottomSheet.value = LocalDate.now()
    }

    fun pickedTaskForBottomSheet(id: Int) = viewModelScope.launch {
        updaterBottomSheet.value = true

        val task = getOneTaskUseCase(id)
        titleForBottomSheet.value =  task.title
        noteForBottomSheet.value = task.note!!
        withoutDateForBottomSheet.value = task.date.toString() == "1970-01-01"
        dateForBottomSheet.value = if(withoutDateForBottomSheet.value) LocalDate.now()
                                    else task.date
        repeatRuleForBottomSheet.value = arrayOf("Нет", "")
        repeatRule.forEach { item ->
            if (item[1] == task.repeatRule) {
                repeatRuleForBottomSheet.value = arrayOf(item[0], item[1])
            }
        }
        idForBottomSheet.value = task.id!!
        checkForBottomSheet.value  = task.check
    }

    fun saveOrUpdateTask() = viewModelScope.launch {
        val task = Task(
            id = if(idForBottomSheet.value == 0) null else idForBottomSheet.value,
            title = titleForBottomSheet.value,
            check = checkForBottomSheet.value,
            date = if(withoutDateForBottomSheet.value) null else dateForBottomSheet.value,
            makeDateTime = LocalDateTime.now(),
            repeatRule = repeatRuleForBottomSheet.value[1],
            note = noteForBottomSheet.value
            )
        if(updaterBottomSheet.value){
            updateTaskUseCase(task)
        } else {
            insertTaskUseCase(task)
        }
    }

    fun editStatus(id: Int, check: Boolean) = viewModelScope.launch{
        editStatusTaskUseCase(id, check)
    }

    fun deleteTask() = viewModelScope.launch{
        deleteTaskUseCase(idForBottomSheet.value)
    }
}