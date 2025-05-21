package com.dailyapps.apiresponse

import com.google.gson.annotations.SerializedName

data class TeachersApiResponse(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("role")
	val role: String? = null,

	)
