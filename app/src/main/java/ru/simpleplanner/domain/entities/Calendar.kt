package ru.simpleplanner.domain.entities

data class Calendar(
    val id: String,
    val displayName: String,
    val color: String,
    val visible: Int,
    val syncEvents: String,
    val timeZone: String
)
