package com.dailyapps.apiresponse

import com.google.gson.annotations.SerializedName

data class AddObservationResponse(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("active")
	val active: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("createAt")
	val createAt: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
