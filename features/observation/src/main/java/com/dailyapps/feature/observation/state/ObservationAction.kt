package com.dailyapps.feature.observation.state

sealed class ObservationAction {
    data class OnGetObservations(val userId: Long, val startDate: String, val endDate: String, val token: String) : ObservationAction()
}