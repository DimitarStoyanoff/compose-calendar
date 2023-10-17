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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.LocalDate

/**
 * A single Day box in the calendar.
 *
 * @param day the represented date.
 */
@Composable
internal fun Day(
    day: LocalDate,
    modifier: Modifier = Modifier,
    isToday: Boolean = false,
    cellSize: Dp = 48.dp,
    backgroundColor: Color = Color.Transparent
) {
    Box(
        modifier = modifier
            .size(cellSize)
            .background(backgroundColor)
    ) {
        val dayModifier = if (isToday) {
            val circleColor = colorResource(R.color.almost_white)
            Modifier.drawBehind { drawCircle(
                color = circleColor,
                radius = this.size.height / 2
            ) }
        } else Modifier
        Text(
            day.dayOfMonth.toString(),
            dayModifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
                .clearAndSetSemantics {},
            color = if (isToday) colorResource(R.color.mine_shaft) else colorResource(R.color.text_white),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.caption
        )
    }
}

@Preview
@Composable
internal fun DayPreview() {
    Day(LocalDate.now(), isToday = true)
}
