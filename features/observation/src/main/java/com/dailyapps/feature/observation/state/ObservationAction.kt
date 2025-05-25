package com.dailyapps.feature.observation.state

sealed class ObservationAction {
    data class OnGetObservations(val userId: Long, val startDate: String, val endDate: String, val token: String) : ObservationAction()
    data class OnGetObservation(val id: Long, val token: String) : ObservationAction()
    data class OnUpdateDateRange(val startDate: String, val endDate: String) : ObservationAction()
    data class OnAddObservation(val name: String, val description: String, val date: String, val lecturerId: Long, val imageUri: String?): ObservationAction()
}