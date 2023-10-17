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

import java.time.YearMonth

/**
 * Data class used to represent a single month week.
 *
 * @param number the consecutive number of the week.
 * @param yearMonth the month this week is from.
 */
data class Week(
    val number: Int,
    val yearMonth: YearMonth
)
