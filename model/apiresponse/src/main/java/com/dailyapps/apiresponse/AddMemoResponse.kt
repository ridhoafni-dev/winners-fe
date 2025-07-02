package com.dailyapps.apiresponse

import com.google.gson.annotations.SerializedName

data class AddMemoResponse(

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("lecturerId")
	val lecturerId: Int? = null,
)
