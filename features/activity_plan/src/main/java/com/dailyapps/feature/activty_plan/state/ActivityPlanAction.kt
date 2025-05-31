package com.dailyapps.feature.activty_plan.state

sealed class ActivityPlanAction {
    data class OnGetActivityPlans(val userId: Long, val startDate: String, val endDate: String, val token: String) : ActivityPlanAction()
    data class OnGetActivityPlan(val id: Long, val token: String) : ActivityPlanAction()
    data class OnUpdateDateRange(val startDate: String, val endDate: String) : ActivityPlanAction()
    data class OnActivityPlanValueChange(val name: String, val startDate: String, val endDate: String, val lecturerId: Long, val status: String): ActivityPlanAction()
    data class OnUpdateActivityPlan(val activityPlanId: Long) : ActivityPlanAction()
    data class OnSubmitReview(val activityPlanId: Long, val rating: Int, val comment: String) : ActivityPlanAction()
    object OnSubmitActivityPlan: ActivityPlanAction()
    object OnResetState : ActivityPlanAction()
}

