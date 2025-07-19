package com.dailyapps.entity

data class SelfReflection(
    val id: Long? = null,
    val title: String? = null,
    val userId: Long? = null,
    val user: User? = null,
    val lecturer: SelfReflectionLecturer? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val comments: List<SelfReflectionComment> = listOf()
)

data class SelfReflectionLecturer(
    val id: Long? = null,
    val selfReflectionId: Long? = null,
    val userId: Long? = null,
    val user: User? = null
)

data class SelfReflectionComment(
    val id: Long? = null,
    val selfReflectionId: Long? = null,
    val userId: Long? = null,
    val user: User? = null,
    val content: String? = null,
    val rating: Int? = null,
    val createdAt: String? = null
)
