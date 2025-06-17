package com.dailyapps.apiresponse

import com.google.gson.annotations.SerializedName

data class LoginApiResponse(

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("nim")
	val nim: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("stase")
	val stase: String? = null,

	@field:SerializedName("endSchoolYear")
	val endSchoolYear: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("startSchoolYear")
	val startSchoolYear: Int? = null,

	@field:SerializedName("token")
	val token: String? = null
)
