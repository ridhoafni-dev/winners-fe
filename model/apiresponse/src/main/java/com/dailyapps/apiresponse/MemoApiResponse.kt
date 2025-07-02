package com.dailyapps.apiresponse

import com.google.gson.annotations.SerializedName

data class MemoApiResponse(

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("active")
	val active: Boolean? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("memoLecturer")
	val memoLecturer: MemoLecturer? = null,

	@field:SerializedName("userId")
	val userId: Int? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("createAt")
	val createAt: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null,

	@field:SerializedName("memoComment")
	val memoCommentResponse: MemoCommentResponse? = null
)

data class MemoCommentResponse(

	@field:SerializedName("rating")
	val rating: Int? = null,

	@field:SerializedName("memoId")
	val memoId: Int? = null,

	@field:SerializedName("comment")
	val comment: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("userId")
	val userId: Int? = null
)

data class MemoLecturer(

	@field:SerializedName("memoId")
	val memoId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("userId")
	val userId: Int? = null
)
