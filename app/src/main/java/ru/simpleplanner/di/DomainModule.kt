package ru.simpleplanner.di

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import ru.simpleplanner.domain.repository.CalendarRepository
import ru.simpleplanner.domain.use_case.calendar_uc.GetCalendarsUseCase

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule {
    @Provides
    @ViewModelScoped
    fun provideGetCalendarsUseCase(calendarRepository: CalendarRepository): GetCalendarsUseCase {
        return GetCalendarsUseCase(calendarRepository = calendarRepository)
    }
}
