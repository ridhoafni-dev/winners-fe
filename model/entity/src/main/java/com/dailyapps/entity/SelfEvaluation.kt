package com.dailyapps.entity

data class SelfEvaluation(
	val selfEvaluationLecturer: SelfEvaluationLecturer? = null,
	val description: String? = null,
	val active: Boolean? = null,
	val id: Int? = null,
	val userId: Int? = null,
	val user: User? = null,
	val createAt: String? = null,
	val updatedAt: String? = null
)

data class SelfEvaluationLecturer(
	val id: Int? = null,
	val selfEvaluationId: Int? = null,
	val userId: Int? = null
)

data class Profile(
	val name: String? = null
)


