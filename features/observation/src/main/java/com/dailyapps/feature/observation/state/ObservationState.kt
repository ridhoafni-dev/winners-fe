package com.dailyapps.feature.observation.state

import com.dailyapps.common.utils.DateUtil
import com.dailyapps.entity.Observation

data class ObservationState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = "",
    val observations: List<Observation> = emptyList(),
    val isSuccess: Boolean = false,
    val isEmpty: Boolean = false,
    val userId: Long = 0,
    val token: String = "",
    val startDate: String = DateUtil.getLastWeekDate(),
    val endDate: String = DateUtil.getCurrentDate(),
) {
    val isUserNotExist: Boolean
        get() = userId == 0L
}