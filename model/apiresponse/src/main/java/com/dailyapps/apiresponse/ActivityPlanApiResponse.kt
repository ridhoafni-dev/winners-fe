package com.dailyapps.apiresponse

import com.google.gson.annotations.SerializedName

data class ActivityPlanApiResponse(

	@field:SerializedName("endDate")
	val endDate: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("active")
	val active: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("activityPlanLecturer")
	val activityPlanLecturer: ActivityPlanLecturer? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("startDate")
	val startDate: String? = null,

	@field:SerializedName("createAt")
	val createAt: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

	@field:SerializedName("activityPlanComment")
	val activityPlanCommentResponse: ActivityPlanCommentResponse? = null
)

data class ActivityPlanCommentResponse(

	@field:SerializedName("rating")
	val rating: Int? = null,

	@field:SerializedName("activityPlanId")
	val activityPlanId: Int? = null,

	@field:SerializedName("comment")
	val comment: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("userId")
	val userId: Int? = null
)

data class ActivityPlanLecturer(

	@field:SerializedName("activityPlanId")
	val activityPlanId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("userId")
	val userId: Int? = null
)

data class User(

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)
