/*
 * Copyright 2023 Voicelip Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created by dstoyanov on 07/03/2023.
 */

package com.voicelip.calendar

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

/**
 * View model for the calendar user activity view.
 */
abstract class CalendarViewModel : ViewModel() {

    protected val calendarActivity = MutableLiveData<CalendarViewUiState>()

    /** Presented calendar view ranges data. */
    val calendarActivityData: LiveData<CalendarViewUiState> = calendarActivity

    /** Currently previewed month. */
    var currentMonth = mutableStateOf(
        createMonth(YearMonth.from(LocalDate.now()))
    )

    /** States which hold calendar ranges for the entire year. */
    protected var calendarUiStates = listOf<CalendarUiState>()

    private val locale = AppCompatDelegate.getApplicationLocales()[0] ?: Locale.getDefault()

    private val dateFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)

    /**
     * Updates the calendar to show the next month.
     */
    fun nextMonthClicked() {
        updateMonth(true)
    }

    /**
     * Updates the calendar to show the previous month.
     */
    fun previousMonthClicked() {
        updateMonth(false)
    }

    /**
     * @return the current month and year title.
     */
    fun getCurrentMonthTitle(): String = currentMonth.value.yearMonth.format(dateFormatter).capitalize(locale)

    /**
     * Changes the month the calendar displays. If the year also changes, fetches data from the server for the new current year.
     *
     * @param isMovedForward true if user clicked next month, false if user clicked previous.
     */
    protected fun updateMonth(isMovedForward: Boolean) {
        val oldYearValue = currentMonth.value.yearMonth.year
        val newMonth = if (isMovedForward) {
            currentMonth.value.yearMonth.plusMonths(1)
        } else {
            currentMonth.value.yearMonth.minusMonths(1)
        }
        currentMonth.value = createMonth(newMonth)
        if (newMonth.year != oldYearValue) {
            fetchNewYearCalendarActivity(newMonth.year)
        } else {
            val newList = filterMonthRanges(calendarUiStates, newMonth)
            calendarActivity.value = CalendarViewUiState.Success(newList)
        }
    }

    /**
     * Creates a new month with weeks to be previewed on demand.
     *
     * @param month the year month that is to be used for the [Month].
     */
    protected fun createMonth(month: YearMonth): Month {
        val numberWeeks = month.getNumberWeeks()
        val listWeekItems = mutableListOf<Week>()
        for (week in 0 until numberWeeks) {
            listWeekItems.add(Week(week, month))
        }
        return Month(month, listWeekItems)
    }

    /**
     * Used for showing ranges only for the current month.
     */
    protected fun filterMonthRanges(ranges: List<CalendarUiState>, month: YearMonth) = ranges.filter {
        it.hasSelectedPeriodOverlap(month.atDay(1),month.atEndOfMonth())
    }

    /**
     * Called when a year changes. Use to fetch your local or remote data and once obtained, update
     * `calendarUiStates` value.
     */
    abstract fun fetchNewYearCalendarActivity(year: Int)

    /**
     * Sets the pill ranges on the calendar.
     *
     * @param data a list of pairs with start date and end date. The dates are in epoch seconds.
     */
    protected fun setSelectedRanges(data: List<Pair<Long, Long?>>) {
        calendarUiStates = data.map { entry ->
            CalendarUiState(
                Instant.ofEpochSecond(entry.first).atZone(ZoneId.systemDefault()).toLocalDate(),
                entry.second?.let { Instant.ofEpochSecond(it).atZone(ZoneId.systemDefault()).toLocalDate() }
            )
        }
    }
}

/**
 * @return the number of weeks in the selected month given a calendar standard. Defaults to [WeekFields.ISO].
 */
fun YearMonth.getNumberWeeks(weekFields: WeekFields = WeekFields.ISO): Int {
    val firstWeekNumber = this.atDay(1)[weekFields.weekOfMonth()]
    val lastWeekNumber = this.atEndOfMonth()[weekFields.weekOfMonth()]
    return lastWeekNumber - firstWeekNumber + 1 // Both weeks inclusive
}

fun String.capitalize(locale: Locale) = replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }

/**
 * Represents calendar server data related to calendar ranges.
 */
sealed interface CalendarViewUiState {
    data class Success(val data: List<CalendarUiState>) : CalendarViewUiState
    data object Loading : CalendarViewUiState
    data object Error : CalendarViewUiState
}