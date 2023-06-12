package ru.simpleplanner.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import ru.simpleplanner.domain.repository.CalendarRepository
import ru.simpleplanner.domain.repository.EventRepository
import ru.simpleplanner.domain.use_case.calendar_uc.GetCalendarsUseCase
import ru.simpleplanner.domain.use_case.event_uc.GetEventsUseCase

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule {
    @Provides
    @ViewModelScoped
    fun provideGetCalendarsUseCase(calendarRepository: CalendarRepository): GetCalendarsUseCase {
        return GetCalendarsUseCase(calendarRepository = calendarRepository)
    }

    @Provides
    @ViewModelScoped
    fun provideGetEventsUseCase(eventRepository: EventRepository): GetEventsUseCase {
        return GetEventsUseCase(eventRepository = eventRepository)
    }
}
