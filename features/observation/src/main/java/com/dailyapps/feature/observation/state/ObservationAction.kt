package com.dailyapps.feature.observation.state

sealed class ObservationAction {
    data class OnGetObservations(val userId: Long, val startDate: String, val endDate: String, val token: String) : ObservationAction()
    data class OnGetObservation(val id: Long, val token: String) : ObservationAction()
    data class OnUpdateDateRange(val startDate: String, val endDate: String) : ObservationAction()
    data class OnObservationValueChange(val name: String, val description: String, val date: String, val lecturerId: Long, val imageUri: String?): ObservationAction()
    data class OnUpdateObservation(val observationId: Long) : ObservationAction()
    data class OnSubmitReview(val observationId: Long, val userId: Long, val rating: Int, val comment: String) : ObservationAction()
    object OnSubmitObservation: ObservationAction()
    object OnResetState : ObservationAction()
}