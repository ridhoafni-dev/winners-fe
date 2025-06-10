package com.dailyapps.feature.observation.state

import com.dailyapps.common.utils.DateUtil
import com.dailyapps.entity.Observation
import com.dailyapps.entity.Teacher

data class ObservationState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isEmpty: Boolean = false,
    val isSuccess: Boolean = false,
    val isIdle: Boolean = true,
    val errorMessage: String = "",
    val token: String = "",
    val userId: Long = 0L,
    val role: String = "",
    val list: ObservationListState = ObservationListState(),
    val detail: ObservationDetailState = ObservationDetailState(),
    val add: FormObservationState = FormObservationState()
) {
    val isUserNotExist: Boolean
        get() = userId == 0L
    val isUserLecturer: Boolean
        get() = role == "LECTURER"
    val isUserStudent: Boolean
        get() = role == "STUDENT"
    val isUserAdmin: Boolean
        get() = role == "ADMIN"
}

data class ObservationDetailState(
    val observation: Observation = Observation()
)

data class ObservationListState(
    val userId: Long = 0,
    val startDate: String = DateUtil.getLastWeekDate(),
    val endDate: String = DateUtil.getCurrentDate(),
    val observations: List<Observation> = emptyList(),
)

data class FormObservationState(
    val lecturers: List<Teacher> = emptyList(),
    val date: String = DateUtil.getCurrentDate(),
    val name: String = "",
    val description: String = "",
    val lecturerId: Long = 0L,
    val image: String = "",
)