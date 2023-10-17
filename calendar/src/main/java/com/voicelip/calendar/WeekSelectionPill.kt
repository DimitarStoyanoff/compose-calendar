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

package io.github.dimitarstoyanoff.calendar

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Draws the RoundedRect red background of the calendar date selection. Animates the selection.
 * CalendarState stores the selectedStartDate and selectedEndDate of selection. These dates are then
 * used to determine a few things: The start offset of the rounded rect drawing, the delay when the pill
 * animation for the week should start and the size of the rounded rect that should be drawn.
 *
 * @param week the current week being drawn.
 * @param currentWeekStart the date of the first day of the week.
 * @param state the selection range being previewed.
 * @param selectedPercentageTotalProvider animation percentage.
 * @param sizePerDay width of a single day view in dp.
 * @param spacerPerDay width of the space between day cells.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeekSelectionPill(
    week: Week,
    currentWeekStart: LocalDate,
    state: CalendarUiState,
    selectedPercentageTotalProvider: () -> Float,
    modifier: Modifier = Modifier,
    sizePerDay: Dp = 48.dp,
    spacerPerDay: Dp = 16.dp
) {
    val sizePerDayPx = with(LocalDensity.current) { sizePerDay.toPx() }
    val spacerPerDayPx = with(LocalDensity.current) { spacerPerDay.toPx() }
    val cornerRadiusPx = with(LocalDensity.current) { 24.dp.toPx() }
    val gradientColors = listOf(
        colorResource(R.color.flame_pea),
        colorResource(R.color.porsche)
    )
    val description = getContentDescription(state)
    Canvas(
        modifier
            .fillMaxWidth()
            .height(sizePerDay),
        description
    ) {
        val (offset, size) = getOffsetAndSize(
            this.size.width,
            state,
            currentWeekStart,
            week,
            sizePerDayPx,
            cornerRadiusPx,
            spacerPerDayPx,
            selectedPercentageTotalProvider()
        )
        val brush = Brush.linearGradient(
            gradientColors,
            start = offset,
            end = Offset(offset.x + size, offset.y)
        )
        drawRoundRect(
            brush,
            offset,
            Size(size, sizePerDayPx),
            CornerRadius(cornerRadiusPx)
        )
    }
}

@Composable
private fun getContentDescription(state: CalendarUiState): String {
    val locale = AppCompatDelegate.getApplicationLocales()[0] ?: Locale.getDefault()
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM", locale)
    return pluralStringResource(
        R.plurals.range_description,
        state.numberSelectedDays.toInt(),
        state.selectedStartDate.format(dateFormatter),
        state.selectedEndDate?.format(dateFormatter) ?: ""
    )
}

/**
 * Calculates the animated Offset and Size of the red selection pill based on the CalendarState and
 * the Week SelectionState, based on the overall selectedPercentage.
 *
 * @param width week row width.
 * @param state the selection range being previewed.
 * @param currentWeekStart the date of the first day of the week.
 * @param week the current week being drawn.
 * @param widthPerDayPx width of a single day view in pixels.
 * @param cornerRadiusPx corner radius of the pill in pixels.
 * @param spacerPerDayPx width of the space between day cells in pixels.
 * @param selectedPercentage animation percentage.
 *
 * @return the beginning offset of the pill and the pill total size.
 */
private fun getOffsetAndSize(
    width: Float,
    state: CalendarUiState,
    currentWeekStart: LocalDate,
    week: Week,
    widthPerDayPx: Float,
    cornerRadiusPx: Float,
    spacerPerDayPx: Float,
    selectedPercentage: Float
): Pair<Offset, Float> {
    val numberDaysSelected = state.getNumberSelectedDaysInWeek(currentWeekStart, week.yearMonth)
    val dayDelay = state.dayDelay(currentWeekStart)
    val edgePadding = (width - (widthPerDayPx * DAYS_IN_WEEK) - (spacerPerDayPx * (DAYS_IN_WEEK - 1))) / 2

    // Calculate length start point offset.
    val percentagePerDay = 1f / state.numberSelectedDays
    val startPercentage = dayDelay * percentagePerDay
    val endPercentage = startPercentage + numberDaysSelected * percentagePerDay

    val scaledPercentage = if (selectedPercentage >= endPercentage) {
        1f
    } else if (selectedPercentage < startPercentage) {
        0f
    } else {
        normalize(
            selectedPercentage,
            startPercentage,
            endPercentage
        )
    }
    val scaledSelectedNumberDays = scaledPercentage * numberDaysSelected

    // Calculate week row side filling.
    val sideSize = edgePadding + cornerRadiusPx
    val leftSize =
        if (state.isLeftHighlighted(currentWeekStart, week.yearMonth)) sideSize else 0f
    val rightSize =
        if (state.isRightHighlighted(currentWeekStart, week.yearMonth)) sideSize else 0f
    var totalSize = (scaledSelectedNumberDays * widthPerDayPx) + ((scaledSelectedNumberDays - 1) * spacerPerDayPx) +
            (leftSize + rightSize) * scaledPercentage
    if (dayDelay == 0 && numberDaysSelected >= 1) {
        totalSize = totalSize.coerceAtLeast(widthPerDayPx)
    }

    val startOffset =
        state.selectedStartOffset(currentWeekStart, week.yearMonth) * (widthPerDayPx + spacerPerDayPx)

    val offset = Offset(startOffset + edgePadding - leftSize, 0f)
    return offset to totalSize
}

/**
 * Scale the overall percentage between the start selection of days to the end selection for
 * the current week. eg: if this week has 3 days before it selected, we only want to
 * start this animation after 3 * percentagePerDay and end it at the number of selected days
 * in the week - so we normalize the percentage between the startPercentage + endPercentage
 * to a range between at min 0f and 1f.
 */
private fun normalize(
    x: Float,
    inMin: Float,
    inMax: Float
): Float {
    val inRange = inMax - inMin
    return (x - inMin) / inRange
}
