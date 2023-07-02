package ru.simpleplanner.domain.repository

interface TimerRepository {
    suspend fun updateTime(work: Long, shortBreak: Long, longBreak: Long, numberOfRepeats: Int)

    suspend fun getTimeWork(): Long

    suspend fun getTimeShortBreak(): Long

    suspend fun getTimeLongBreak(): Long

    suspend fun getNumberOfRepeats(): Int
}