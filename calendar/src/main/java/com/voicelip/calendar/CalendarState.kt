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
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

/** Holds the state of the calendar. */
class CalendarState {

    /** States which hold calendar ranges. */
    val calendarUiState = mutableStateOf(listOf<CalendarUiState>())

    /** State of the progress view. */
    val loadingState = mutableStateOf(false)

    /** Currently cached months. */
    var currentMonth = mutableStateOf(
        createMonth(YearMonth.from(LocalDate.now()))
    )

    private val locale = AppCompatDelegate.getApplicationLocales()[0] ?: Locale.getDefault()

    private val dateFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", locale)

    /**
     * Updates the weeks to show the next month.
     */
    internal fun nextMonthClicked() {
        currentMonth.value = createMonth(currentMonth.value.yearMonth.plusMonths(1))
    }

    /**
     * Updates the weeks to show the previous month.
     */
    internal fun previousMonthClicked() {
        currentMonth.value = createMonth(currentMonth.value.yearMonth.minusMonths(1))
    }

    /**
     * @return the current month and year.
     */
    internal fun getCurrentMonthTitle(): String = currentMonth.value.yearMonth.format(dateFormatter).capitalize(locale)

    /**
     * Creates a new month with weeks to be previewed on demand.
     *
     * @param month the year month that is to be used for the [Month].
     */
    private fun createMonth(month: YearMonth): Month {
        val numberWeeks = month.getNumberWeeks()
        val listWeekItems = mutableListOf<Week>()
        for (week in 0 until numberWeeks) {
            listWeekItems.add(Week(week, month))
        }
        return Month(month, listWeekItems)
    }

    /**
     * Sets the pill ranges on the calendar.
     *
     * @param data a list of pairs with start date and end date. The dates are in epoch seconds.
     */
    fun setSelectedRanges(data: List<Pair<Long, Long?>>) {
        calendarUiState.value = data.map { entry ->
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