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
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.text.DateFormatSymbols
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters
import java.util.Calendar
import java.util.Locale

@Composable
fun CalendarView(
    modifier: Modifier = Modifier,
    currentMonth: MutableState<Month>,
    calendarRangesState: State<CalendarViewUiState?>,
    currentMonthTitleProvider: () -> String,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cellSize = screenWidth / 15
    val cellSpacerSize = screenWidth / 20
    Box(
        modifier
            .clip(RoundedCornerShape(15.dp))
            .background(colorResource(id = R.color.mine_shaft))
    ) {
        Column(
            Modifier.wrapContentSize()
        ) {
            CalendarHeader(
                currentMonthTitleProvider(),
                onPreviousClick,
                onNextClick
            )
            DaysOfWeekHeader(cellSize, cellSpacerSize)
            Weeks(currentMonth, calendarRangesState.value, cellSize, cellSpacerSize)
            if (calendarRangesState.value is CalendarViewUiState.Loading)
                LinearProgressIndicator(Modifier.fillMaxWidth(), colorResource(R.color.flame_pea))
        }
    }
}

@Composable
fun Weeks(
    currentMonth: MutableState<Month>,
    calendarRangesState: CalendarViewUiState?,
    cellSize: Dp = 24.dp,
    cellSpacerSize: Dp = 16.dp
) {
    Column {
        val contentModifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
        currentMonth.value.weeks.forEach { week ->
            Box {
                if (calendarRangesState is CalendarViewUiState.Success) {
                    calendarRangesState.data.forEach { calendarUiState ->
                        PillRange(calendarUiState, week, cellSize, cellSpacerSize)
                    }
                }
                Week(
                    modifier = contentModifier,
                    week = week,
                    cellSize = cellSize,
                    cellSpacerSize = cellSpacerSize
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PillRange(
    calendarUiState: CalendarUiState,
    week: Week,
    cellSize: Dp = 24.dp,
    cellSpacerSize: Dp = 16.dp
) {
    val beginningWeek = week.yearMonth.atDay(1).plusWeeks(week.number.toLong())
    val currentDay = beginningWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    val numberSelectedDays = calendarUiState.numberSelectedDays.toInt()
    val selectedAnimationPercentage = remember(numberSelectedDays) {
        Animatable(0f)
    }
    val durationMillisPerDay = 150
    LaunchedEffect(numberSelectedDays) {
        val animationSpec: TweenSpec<Float> = tween(
            durationMillis =
            (numberSelectedDays.coerceAtLeast(0) * durationMillisPerDay).coerceAtMost(2000),
            easing = EaseOutQuart
        )
        selectedAnimationPercentage.animateTo(
            targetValue = 1f,
            animationSpec = animationSpec
        )
    }

    if (calendarUiState.hasSelectedPeriodOverlap(
            currentDay,
            currentDay.plusDays(6)
        )
    ) {
        WeekSelectionPill(
            state = calendarUiState,
            currentWeekStart = currentDay,
            sizePerDay = cellSize,
            spacerPerDay = cellSpacerSize,
            week = week,
            selectedPercentageTotalProvider = { selectedAnimationPercentage.value }
        )
    }
}

@Composable
private fun CalendarHeader(
    currentMonth: String,
    onBackClick: () -> Unit,
    onForwardClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(24.dp, 24.dp, 24.dp),
        Arrangement.SpaceBetween,
        Alignment.CenterVertically
    ) {
        Image(
            painterResource(R.drawable.ic_arrow_left),
            stringResource(R.string.calendar_back_button_description),
            Modifier.clickable(onClick = onBackClick)
        )
        Text(
            currentMonth,
            color = colorResource(R.color.text_white),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.button
        )
        Image(
            painterResource(R.drawable.ic_arrow_right),
            stringResource(R.string.calendar_forward_button_description),
            Modifier.clickable(onClick = onForwardClick)
        )
    }
}

@Composable
private fun DaysOfWeekHeader(cellSize: Dp = 24.dp, cellSpacerSize: Dp = 16.dp) {
    val locale = AppCompatDelegate.getApplicationLocales()[0] ?: Locale.getDefault()
    val shortDaysInEveryWeek = DateFormatSymbols(locale).shortWeekdays.map { it.capitalize(locale) }
    Row(
        Modifier
            .wrapContentWidth()
            .padding(top = 16.dp, bottom = 20.dp),
        Arrangement.Start,
        Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.weight(1f))
        val labelModifier = Modifier
            .widthIn(cellSize)
            .clearAndSetSemantics {}
        for (index in 2 until shortDaysInEveryWeek.size) {
            DaysOfWeekText(shortDaysInEveryWeek[index], labelModifier)
            Spacer(Modifier.size(cellSpacerSize))
        }
        DaysOfWeekText(shortDaysInEveryWeek[Calendar.SUNDAY], labelModifier)
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun DaysOfWeekText(text: String, modifier: Modifier = Modifier) = Text(
    text,
    modifier,
    color = colorResource(R.color.text_white),
    fontWeight = FontWeight.Bold,
    textAlign = TextAlign.Center,
    style = MaterialTheme.typography.caption
)

@Preview
@Composable
fun CalendarViewPreview() {
    val viewModel = object: CalendarViewModel() {
        override fun fetchNewYearCalendarActivity(year: Int) {

        }
    }
    val state = remember {
        mutableStateOf(viewModel.calendarActivityData.value)
    }

    CalendarView(
        currentMonth = viewModel.currentMonth,
        calendarRangesState = state,
        currentMonthTitleProvider = { viewModel.getCurrentMonthTitle() },
        onPreviousClick = {},
        onNextClick = {}
    )
}