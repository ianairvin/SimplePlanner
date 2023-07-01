package ru.simpleplanner.domain.repository

import kotlinx.coroutines.flow.Flow

interface TimerRepository {
    suspend fun updateTime(work: Long, shortBreak: Long, longBreak: Long)

    suspend fun getTimeWork(): Long

    suspend fun getTimeShortBreak(): Long

    suspend fun getTimeLongBreak(): Long
}