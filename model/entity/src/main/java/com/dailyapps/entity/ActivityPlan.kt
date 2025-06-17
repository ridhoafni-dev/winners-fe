package com.dailyapps.entity

data class ActivityPlan(
	val endDate: String? = null,
	val name: String? = null,
	val active: Boolean? = null,
	val id: Int? = null,
	val activityPlanLecturer: ActivityPlanLecturer? = null,
	val userId: Int? = null,
	val user: User? = null,
	val startDate: String? = null,
	val createAt: String? = null,
	val status: String? = null,
	val updatedAt: String? = null,
	val activityPlanComment: ActivityPlanComment? = null
)

data class ActivityPlanComment(
	val rating: Int? = null,
	val activityPlanId: Int? = null,
	val comment: String? = null,
	val id: Int? = null,
	val userId: Int? = null
)

data class ActivityPlanLecturer(
	val userId: Int? = null
)
