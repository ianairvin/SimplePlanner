package ru.simpleplanner.presentation.task_screen

import android.annotation.SuppressLint
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
    var tasksListDone = getTasksUseCase("Done")

    val titleForBottomSheet =  mutableStateOf("")
    val priorityForBottomSheet =  mutableStateOf(arrayOf("Нет", "0"))
    val noteForBottomSheet = mutableStateOf("")
    val dateForBottomSheet =  mutableStateOf(LocalDate.now())
    private val idForBottomSheet =  mutableIntStateOf(0)
    private val checkForBottomSheet =  mutableStateOf(false)
    val withoutDateForBottomSheet =  mutableStateOf(false)

    val updaterBottomSheet = mutableStateOf(false)

    val priority = arrayOf(
        arrayOf("Нет", "0"),
        arrayOf("Красный", "1"),
        arrayOf("Желтый", "2"),
        arrayOf("Зеленый", "3"),
        arrayOf("Голубой", "4"),
        arrayOf("Фиолетовый", "5")
    )

    fun newTaskForBottomSheet(){
        updaterBottomSheet.value = false

        titleForBottomSheet.value =  ""
        priorityForBottomSheet.value =  arrayOf("Нет", "0")
        noteForBottomSheet.value = ""
        dateForBottomSheet.value = LocalDate.now()
    }

    @SuppressLint("AutoboxingStateValueProperty")
    fun pickedTaskForBottomSheet(id: Int) = viewModelScope.launch {
        updaterBottomSheet.value = true

        val task = getOneTaskUseCase(id)
        titleForBottomSheet.value =  task.title
        noteForBottomSheet.value = task.note!!
        withoutDateForBottomSheet.value = task.date == null
        dateForBottomSheet.value = if(withoutDateForBottomSheet.value) LocalDate.now()
                                    else task.date
        priorityForBottomSheet.value = arrayOf("Нет", "0")
        priority.forEach { item ->
            if (item[1] == task.priority.toString()) {
                priorityForBottomSheet.value = arrayOf(item[0], item[1])
            }
        }
        idForBottomSheet.value = task.id!!
        checkForBottomSheet.value  = task.check
    }

    @SuppressLint("AutoboxingStateValueProperty")
    fun saveOrUpdateTask() = viewModelScope.launch {
        val task = Task(
            id = if(idForBottomSheet.value == 0) null else idForBottomSheet.value,
            title = titleForBottomSheet.value,
            check = checkForBottomSheet.value,
            date = if(withoutDateForBottomSheet.value) null else dateForBottomSheet.value,
            makeDateTime = LocalDateTime.now(),
            note = noteForBottomSheet.value,
            priority = priorityForBottomSheet.value[1].toInt()
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

    @SuppressLint("AutoboxingStateValueProperty")
    fun deleteTask() = viewModelScope.launch{
        deleteTaskUseCase(idForBottomSheet.value)
    }
}