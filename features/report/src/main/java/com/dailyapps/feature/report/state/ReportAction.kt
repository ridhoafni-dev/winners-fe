package com.dailyapps.feature.report.state

sealed class ReportAction {
    data class OnGetReports(
        val userId: Long,
        val startDate: String,
        val endDate: String,
        val token: String
    ) : ReportAction()

    data class OnUpdateDateRange(val startDate: String, val endDate: String) : ReportAction()
    data class OnDownloadReport(val id: Long, val token: String) : ReportAction()
    object OnResetDownloadState : ReportAction()
    object OnResetState : ReportAction()

    // New form actions
    data class OnReportValueChange(
        val date: String,
        val lecturerId: Long,
        val documentUri: String?
    ) : ReportAction()

    object OnSubmitReport : ReportAction()

    // Actions for edit/update functionality
    data class OnGetReport(val id: Long, val token: String) : ReportAction()
    data class OnUpdateReport(val id: Long) : ReportAction()
}
