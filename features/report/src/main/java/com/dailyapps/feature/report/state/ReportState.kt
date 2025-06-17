package com.dailyapps.feature.report.state

import com.dailyapps.common.utils.DateUtil
import com.dailyapps.entity.Report

data class ReportState(
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isEmpty: Boolean = false,
    val isSuccess: Boolean = false,
    val isIdle: Boolean = true,
    val errorMessage: String = "",
    val token: String = "",
    val userId: Long = 0L,
    val role: String = "",
    val list: ReportListState = ReportListState(),
    val downloading: Boolean = false,
    val downloadSuccess: Boolean = false,
    val downloadError: Boolean = false,
    val downloadErrorMessage: String = "",
    val downloadUrl: String = ""
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

data class ReportListState(
    val userId: Long = 0,
    val startDate: String = DateUtil.getLastWeekDate(),
    val endDate: String = DateUtil.getCurrentDate(),
    val reports: List<Report> = emptyList(),
)