package ru.simpleplanner.data.repository

import ru.simpleplanner.data.room.Dao
import ru.simpleplanner.data.room.TimerDB
import ru.simpleplanner.domain.repository.TimerRepository
import javax.inject.Inject

class TimerRepositoryImpl @Inject constructor (
    private val dao: Dao
): TimerRepository {
    override suspend fun updateTime(work: Long, shortBreak: Long, longBreak: Long, numberOfRepeats: Int) {
        val timer = TimerDB(
            1,
            work,
            shortBreak,
            longBreak,
            numberOfRepeats
        )
        dao.updateTime(timer)
    }

    override suspend fun getTimeWork(): Long {
        return dao.getTimeWork()
    }

    override suspend fun getTimeShortBreak(): Long {
        return dao.getTimeShortBreak()
    }

    override suspend fun getTimeLongBreak(): Long {
        return dao.getTimeLongBreak()
    }

    override suspend fun getNumberOfRepeats(): Int{
        return dao.getNumberOfRepeats()
    }
}