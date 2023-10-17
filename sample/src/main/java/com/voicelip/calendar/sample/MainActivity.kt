package io.github.dimitarstoyanoff.calendar.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.dimitarstoyanoff.calendar.CalendarView
import io.github.dimitarstoyanoff.calendar.sample.ui.theme.ComposeCalendarTheme
import io.github.dimitarstoyanoff.calendar.sample.ui.theme.Gray12

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeCalendarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Gray12
                ) {
                    CalendarSample()
                }
            }
        }
    }
}

@Preview
@Composable
fun CalendarSample() {
    val calendarViewModel: MyCalendarViewModel = viewModel()
    CalendarView(
        Modifier.wrapContentSize(),
        currentMonth = calendarViewModel.currentMonth,
        calendarRangesState = calendarViewModel.calendarActivityData.observeAsState(),
        currentMonthTitleProvider = { calendarViewModel.getCurrentMonthTitle() },
        onPreviousClick = { calendarViewModel.previousMonthClicked() },
        onNextClick = { calendarViewModel.nextMonthClicked() }
    )
}