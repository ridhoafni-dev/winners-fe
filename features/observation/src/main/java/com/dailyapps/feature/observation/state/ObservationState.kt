package com.dailyapps.feature.observation.state

import com.dailyapps.common.utils.DateUtil
import com.dailyapps.entity.Observation

data class ObservationState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isEmpty: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String = "",
    val token: String = "",
    val userId: Long = 0L,
    val list: ObservationListState = ObservationListState(),
    val detail: ObservationDetailState = ObservationDetailState(),
    val add: AddObservationState = AddObservationState()
)

data class ObservationDetailState(
    val observation: Observation = Observation()
)

data class ObservationListState(
    val userId: Long = 0,
    val startDate: String = DateUtil.getLastWeekDate(),
    val endDate: String = DateUtil.getCurrentDate(),
    val observations: List<Observation> = emptyList(),
) {
    val isUserNotExist: Boolean
        get() = userId == 0L
}

data class AddObservationState(
    val date: String = DateUtil.getCurrentDate(),
    val name: String = "",
    val description: String = "",
    val lecturer: Long = 0L,
    val image: String = "",
)