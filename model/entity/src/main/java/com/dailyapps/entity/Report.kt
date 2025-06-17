package com.dailyapps.entity

data class Report(
	val date: String? = null,
	val image: String? = null,
	val active: Boolean? = null,
	val id: Int? = null,
	val userId: Int? = null,
	val user: User? = null,
	val reportLecturer: ReportLecturer? = null,
	val createAt: String? = null,
	val updatedAt: String? = null
)

data class ReportLecturer(
	val reportId: Int? = null,
	val id: Int? = null,
	val userId: Int? = null
)

