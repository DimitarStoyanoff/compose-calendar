package com.voicelip.calendar.sample

import androidx.lifecycle.viewModelScope
import com.voicelip.calendar.CalendarUiState
import com.voicelip.calendar.CalendarViewModel
import com.voicelip.calendar.CalendarViewUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Date

class MyCalendarViewModel : CalendarViewModel() {

    init {
        fetchNewYearCalendarActivity(0)
    }

    override fun fetchNewYearCalendarActivity(year: Int) {
        viewModelScope.launch {
            calendarActivity.value = CalendarViewUiState.Loading
            delay(2000)
            calendarUiStates = getSampleDataWithDesugar()
//            setSampleDataWithoutDesugar()
            calendarActivity.value = CalendarViewUiState.Success(
                filterMonthRanges(
                    calendarUiStates,
                    currentMonth.value.yearMonth
                )
            )
        }
    }

    /**
     * If you're targeting below API 26 and are not using desugaring you can set data this way.
     */
    private fun setSampleDataWithoutDesugar() {
        val todayLongInSeconds = Date().time / 1000
        val dayInSeconds = 60 * 60 * 24
        setSelectedRanges(
            listOf(
                Pair(todayLongInSeconds,
                    todayLongInSeconds + (dayInSeconds * 3)
                ),
                Pair(
                    todayLongInSeconds + (dayInSeconds * 17),
                    todayLongInSeconds + (dayInSeconds * 26)
                )
            )
        )
    }

    /**
     * If you're using desugaring in your app module or are targeting API 26 and above you can set data this way.
     */
    private fun getSampleDataWithDesugar() = listOf(
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