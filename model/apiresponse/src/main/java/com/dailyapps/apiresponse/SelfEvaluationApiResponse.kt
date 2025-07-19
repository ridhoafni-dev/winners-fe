package com.dailyapps.apiresponse

import com.google.gson.annotations.SerializedName

data class SelfEvaluationApiResponse(

	@field:SerializedName("selfEvaluationLecturer")
	val selfEvaluationLecturer: SelfEvaluationLecturerApiResponse? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("active")
	val active: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("user")
	val user: UserApiResponse? = null,

	@field:SerializedName("createAt")
	val createAt: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class Profile(

	@field:SerializedName("name")
	val name: String? = null
)

data class SelfEvaluationLecturerApiResponse(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("selfEvaluationId")
	val selfEvaluationId: Int? = null,

	@field:SerializedName("userId")
	val userId: Int? = null
)

