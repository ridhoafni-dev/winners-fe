package com.dailyapps.entity

data class Memo(
	val memoComment: MemoComment? = null,
	val active: Boolean? = null,
	val memoLecturer: MemoLecturer? = null,
	val id: Int? = null,
	val title: String? = null,
	val userId: Int? = null,
	val user: User? = null,
	val createAt: String? = null,
	val updatedAt: String? = null
)

data class MemoComment(
	val memoId: Int? = null,
	val rating: Int? = null,
	val comment: String? = null,
	val id: Int? = null,
	val userId: Int? = null
)

data class MemoLecturer(
	val memoId: Int? = null,
	val id: Int? = null,
	val userId: Int? = null
)

