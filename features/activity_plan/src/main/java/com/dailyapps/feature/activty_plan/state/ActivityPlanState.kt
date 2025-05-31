package com.dailyapps.feature.activty_plan.state

import com.dailyapps.common.utils.DateUtil
import com.dailyapps.entity.ActivityPlan
import com.dailyapps.entity.Observation
import com.dailyapps.entity.Teacher

data class ActivityPlanState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isEmpty: Boolean = false,
    val isSuccess: Boolean = false,
    val isIdle: Boolean = true,
    val errorMessage: String = "",
    val token: String = "",
    val userId: Long = 0L,
    val role: String = "",
    val list: ActivityPlanListState = ActivityPlanListState(),
    val detail: ActivityPlanDetailState = ActivityPlanDetailState(),
    val add: FormActivityPlanState = FormActivityPlanState()
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

data class ActivityPlanDetailState(
    val activityPlan: ActivityPlan = ActivityPlan()
)

data class ActivityPlanListState(
    val userId: Long = 0,
    val startDate: String = DateUtil.getLastWeekDate(),
    val endDate: String = DateUtil.getCurrentDate(),
    val activityPlans: List<ActivityPlan> = emptyList(),
)

data class FormActivityPlanState(
    val lecturers: List<Teacher> = emptyList(),
    val startDate: String = DateUtil.getCurrentDate(),
    val endDate: String = "",
    val name: String = "",
    val lecturerId: Long = 0L,
)