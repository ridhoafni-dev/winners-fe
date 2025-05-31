package com.dailyapps.apiresponse

import com.google.gson.annotations.SerializedName

data class AddActivityPlanResponse(

	@field:SerializedName("endDate")
	val endDate: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("active")
	val active: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("startDate")
	val startDate: String? = null,

	@field:SerializedName("createAt")
	val createAt: String? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
