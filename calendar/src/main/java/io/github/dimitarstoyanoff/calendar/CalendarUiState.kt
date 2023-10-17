/*
 * Copyright 2023 dimitarstoyanoff.
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

package io.github.dimitarstoyanoff.calendar

import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit
import kotlin.math.abs

/** Constant for convenience. */
internal const val DAYS_IN_WEEK = 7

/**
 * Represents a single calendar range. Has functions which help determine its visual pill representation.
 *
 * @param selectedStartDate the beginning date of the range.
 * @param selectedEndDate the end date of the range.
 */
data class CalendarUiState(
    val selectedStartDate: LocalDate,
    val selectedEndDate: LocalDate? = null
) {
    /** The total days in the range. */
    val numberSelectedDays: Float
        get() {
            if (selectedEndDate == null) return 1f
            return ChronoUnit.DAYS.between(selectedStartDate, selectedEndDate.plusDays(1)).toFloat()
        }

    /**
     * Determines if this range overlaps with a given other range.
     *
     * @param start beginning of compared range.
     * @param end end of compared range.
     *
     * @return true if the ranges overlap somewhere.
     */
    fun hasSelectedPeriodOverlap(start: LocalDate, end: LocalDate): Boolean {
        if (selectedStartDate == start || selectedStartDate == end) return true
        if (selectedEndDate == null) {
            return !selectedStartDate.isBefore(start) && !selectedStartDate.isAfter(end)
        }
        return !end.isBefore(selectedStartDate) && !start.isAfter(selectedEndDate)
    }

    /**
     * Determines if a given date is in this range.
     *
     * @param date the checked date.
     *
     * @return true if it's in the range.
     */
    private fun isDateInSelectedPeriod(date: LocalDate): Boolean {
        if (selectedStartDate == date) return true
        if (selectedEndDate == null) return false
        if (date.isBefore(selectedStartDate) || date.isAfter(selectedEndDate)) return false
        return true
    }

    /**
     * @param currentWeekStartDate the beginning of the given week.
     * @param month the month that the week is from.
     *
     * @return the number of days that this range covers in a given week.
     */
    fun getNumberSelectedDaysInWeek(currentWeekStartDate: LocalDate, month: YearMonth): Int {
        var countSelected = 0
        var currentDate = currentWeekStartDate
        for (i in 0 until DAYS_IN_WEEK) {
            if (isDateInSelectedPeriod(currentDate) && currentDate.month == month.month) {
                countSelected++
            }
            currentDate = currentDate.plusDays(1)
        }
        return countSelected
    }

    /**
     * Returns the number of days that are not in this range from the start or end of the week.
     *
     * @param currentWeekStartDate the beginning of the given week.
     * @param yearMonth the month that the week is from.
     */
    fun selectedStartOffset(currentWeekStartDate: LocalDate, yearMonth: YearMonth): Int {
        var startDate = currentWeekStartDate
        var startOffset = 0
        for (i in 0 until DAYS_IN_WEEK) {
            if (!isDateInSelectedPeriod(startDate) || startDate.month != yearMonth.month) {
                startOffset++
            } else {
                break
            }
            startDate = startDate.plusDays(1)
        }
        return startOffset
    }

    /**
     * Determines whether the selection continues on the left side on the current week row.
     *
     * @param beginningWeek the beginning of the given week.
     * @param month the month that the week is from.
     */
    fun isLeftHighlighted(beginningWeek: LocalDate, month: YearMonth): Boolean {
        return if (month.month.value != beginningWeek.month.value) {
            false
        } else {
            val beginningWeekSelected = isDateInSelectedPeriod(beginningWeek)
            val lastDayPreviousWeek = beginningWeek.minusDays(1)
            isDateInSelectedPeriod(lastDayPreviousWeek) && beginningWeekSelected
        }
    }

    /**
     * Determines whether the selection continues on the right side on the current week row.
     *
     * @param beginningWeek the beginning of the given week.
     * @param month the month that the week is from.
     */
    fun isRightHighlighted(
        beginningWeek: LocalDate,
        month: YearMonth
    ): Boolean {
        val lastDayOfTheWeek = beginningWeek.plusDays(6)
        return if (month.month.value != lastDayOfTheWeek.month.value) {
            false
        } else {
            val lastDayOfTheWeekSelected = isDateInSelectedPeriod(lastDayOfTheWeek)
            val firstDayNextWeek = lastDayOfTheWeek.plusDays(1)
            isDateInSelectedPeriod(firstDayNextWeek) && lastDayOfTheWeekSelected
        }
    }

    /**
     * Calculates the animation delay for the current week depending on how many days the selection covers before the start of the
     * given week.
     *
     * @param currentWeekStartDate the start date of the given week.
     */
    fun dayDelay(currentWeekStartDate: LocalDate): Int {
        if (selectedEndDate == null) return 0
        // if selected week contains start date, don't have any delay
        val endWeek = currentWeekStartDate.plusDays(6)
        return if (selectedStartDate.isBefore(currentWeekStartDate) || selectedStartDate.isAfter(endWeek)) {
            // selected start date is not in current week
            abs(ChronoUnit.DAYS.between(currentWeekStartDate, selectedStartDate)).toInt()
        } else {
            0
        }
    }
}