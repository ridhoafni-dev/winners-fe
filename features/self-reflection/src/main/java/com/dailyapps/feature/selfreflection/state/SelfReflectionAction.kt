package com.dailyapps.feature.selfreflection.state

/**
 * Actions that can be performed in the Self-Reflection feature
 */
sealed class SelfReflectionAction {
    data class LoadSelfReflection(val id: Long) : SelfReflectionAction()
    data class FilterByDate(val startDate: String, val endDate: String) : SelfReflectionAction()
    data class GetSelfReflections(
        val userId: Long,
        val startDate: String,
        val endDate: String,
        val token: String,
        val lecturer: Boolean = false
    ) : SelfReflectionAction()
    data class UpdateDateRange(val startDate: String, val endDate: String) : SelfReflectionAction()
    data class AddComment(
        val reflectionId: Long,
        val userId: Long,
        val comment: String,
        val rating: Int
    ) : SelfReflectionAction()

    // Actions for SelfReflectionFormScreen
    data class OnGetSelfReflection(val id: Long, val token: String) : SelfReflectionAction()
    data class OnSelfReflectionValueChange(val title: String, val lecturerId: Long) : SelfReflectionAction()
    object OnSubmitSelfReflection : SelfReflectionAction()
    data class OnUpdateSelfReflection(val id: Long) : SelfReflectionAction()
    object OnResetState : SelfReflectionAction()
}
