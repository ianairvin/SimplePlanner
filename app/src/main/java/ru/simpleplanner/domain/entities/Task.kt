package ru.simpleplanner.domain.entities

import java.time.LocalDate
import java.time.LocalDateTime
data class Task(
    var id: Int?,
    var title: String,
    var check: Boolean,
    var date: LocalDate?,
    var makeDateTime: LocalDateTime?,
    var note: String?,
    var priority: Int
)
