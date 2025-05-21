package com.dailyapps.apiresponse

import com.google.gson.annotations.SerializedName

data class ObservationApiResponse(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("observationComments")
	val observationComments: ObservationComments? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("active")
	val active: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("createAt")
	val createAt: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)

data class User(

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null
)

data class ObservationComments(

	@field:SerializedName("observationId")
	val observationId: Int? = null,

	@field:SerializedName("rating")
	val rating: Int? = null,

	@field:SerializedName("comment")
	val comment: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("userId")
	val userId: Int? = null
)
