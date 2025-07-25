package com.dailyapps.entity

data class SelfReflection(
    val id: Long? = null,
    val title: String? = null,
    val active: Boolean? = null,
    val userId: Long? = null,
    val user: User? = null,
    val createAt: String? = null,
    val updatedAt: String? = null,
    val selfReflectionLecturer: SelfReflectionLecturer? = null,
    val selfReflectionComment: SelfReflectionComment? = null
)

data class SelfReflectionLecturer(
    val userId: Long? = null,
    val name: String? = null,
    val id: Long? = null,
)

data class SelfReflectionComment(
    val rating: Int? = null,
    val comment: String? = null,
    val id: Int? = null,
    val userId: Int? = null
)
