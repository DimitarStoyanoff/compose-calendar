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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

/**
 * Represents a week row in the calendar.
 *
 * @param week the previewed week.
 */
@Composable
internal fun Week(
    week: Week,
    modifier: Modifier = Modifier,
    cellSize: Dp = 24.dp,
    cellSpacerSize: Dp = 16.dp
) {
    val beginningWeek = week.yearMonth.atDay(1).plusWeeks(week.number.toLong())
    var currentDay = beginningWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val today = LocalDate.now()

    Row(modifier = modifier) {
        Spacer(
            Modifier
                .weight(1f)
                .heightIn(max = cellSize)
        )
        for (i in 0..6) {
            if (currentDay.month == week.yearMonth.month) {
                Day(
                    day = currentDay,
                    isToday = currentDay.isEqual(today),
                    cellSize = cellSize
                )
            } else {
                Box(modifier = Modifier.size(cellSize))
            }
            currentDay = currentDay.plusDays(1)
            if (i != 6)
                Spacer(modifier = Modifier.size(cellSpacerSize))
        }
        Spacer(
            Modifier
                .weight(1f)
                .heightIn(max = cellSize)
        )
    }
}

@Preview
@Composable
internal fun WeekPreview() {
    Week(week = Week(1, YearMonth.now()))
}