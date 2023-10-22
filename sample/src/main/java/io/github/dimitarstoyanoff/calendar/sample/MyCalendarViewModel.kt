package io.github.dimitarstoyanoff.calendar.sample

import androidx.lifecycle.viewModelScope
import io.github.dimitarstoyanoff.calendar.CalendarUiState
import io.github.dimitarstoyanoff.calendar.CalendarViewModel
import io.github.dimitarstoyanoff.calendar.CalendarViewUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

class MyCalendarViewModel : CalendarViewModel() {

    init {
        fetchNewYearCalendarActivity(0)
    }

    override fun fetchNewYearCalendarActivity(year: Int) {
        viewModelScope.launch {
            calendarActivity.value = CalendarViewUiState.Loading
            delay(2000)
            calendarUiStates = getSampleData()
            calendarActivity.value = CalendarViewUiState.Success(
                filterMonthRanges(
                    calendarUiStates,
                    currentMonth.value.yearMonth
                )
            )
        }
    }

    private fun getSampleData() = listOf(
        CalendarUiState(
            LocalDate.now(),
            LocalDate.now().plusDays(3)
        ),
        CalendarUiState(
            LocalDate.now().plusDays(17),
            LocalDate.now().plusDays(26)
        )
    )
}