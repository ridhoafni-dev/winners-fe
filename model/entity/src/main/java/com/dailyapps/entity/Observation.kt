package com.dailyapps.entity

data class Observation(
	val date: String? = null,
	val image: String? = null,
	val name: String? = null,
	val observationComments: ObservationComments? = null,
	val observationLecturer: ObservationLecturer? = null,
	val description: String? = null,
	val active: Boolean? = null,
	val id: Int? = null,
	val userId: Int? = null,
	val user: User? = null,
	val createAt: String? = null,
	val updatedAt: String? = null
)

data class ObservationComments(
	val observationId: Int? = null,
	val rating: Int? = null,
	val comment: String? = null,
	val id: Int? = null,
	val userId: Int? = null
)

data class ObservationLecturer(
	val userId: Long? = null
)

