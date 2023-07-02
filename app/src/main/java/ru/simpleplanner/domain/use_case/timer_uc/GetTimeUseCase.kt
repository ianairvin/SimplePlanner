package ru.simpleplanner.domain.use_case.timer_uc

import kotlinx.coroutines.flow.Flow
import ru.simpleplanner.domain.repository.TimerRepository
import javax.inject.Inject

class GetTimeUseCase @Inject constructor(
    private val timerRepository : TimerRepository
) {
    suspend fun getTimeWork(): Long {
        return timerRepository.getTimeWork()
    }

    suspend fun getTimeShortBreak(): Long {
        return timerRepository.getTimeShortBreak()
    }

    suspend fun getTimeLongBreak(): Long {
        return timerRepository.getTimeLongBreak()
    }

    suspend fun getNumberOfRepeats(): Int{
        return timerRepository.getNumberOfRepeats()
    }
}