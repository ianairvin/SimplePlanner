package ru.simpleplanner.domain.use_case.timer_uc

import ru.simpleplanner.domain.repository.TimerRepository
import javax.inject.Inject

class UpdateTimeUseCase @Inject constructor(
    private val timerRepository : TimerRepository
) {
    suspend operator fun invoke(work: Long, shortBreak: Long, longBreak: Long){
        return timerRepository.updateTime(work, shortBreak, longBreak)
    }
}