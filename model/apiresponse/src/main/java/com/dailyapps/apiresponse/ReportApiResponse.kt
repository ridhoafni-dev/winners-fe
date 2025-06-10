package com.dailyapps.apiresponse

import com.google.gson.annotations.SerializedName

data class ReportApiResponse(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("active")
	val active: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("user")
	val user: UserApiResponse? = null,

	@field:SerializedName("reportLecturer")
	val reportLecturer: ReportLecturerApiResponse? = null,

	@field:SerializedName("createAt")
	val createAt: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class ReportLecturerApiResponse(

	@field:SerializedName("reportId")
	val reportId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("userId")
	val userId: Int? = null
)
