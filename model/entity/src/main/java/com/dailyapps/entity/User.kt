package com.dailyapps.entity

data class User(
    val role: String? = null,
    val address: String? = null,
    val nim: String? = null,
    val name: String? = null,
    val stase: String? = null,
    val endSchoolYear: Int? = null,
    val id: Int? = null,
    val email: String? = null,
    val startSchoolYear: Int? = null,
    val token: String? = null
) {
    companion object {
        val EMPTY = User(
            role = null,
            address = null,
            nim = null,
            name = null,
            stase = null,
            endSchoolYear = null,
            id = null,
            email = null,
            startSchoolYear = null,
            token = null
        )
    }
}

val User.isStudent
    get() = role == "STUDENT"
val User.isLecturer
    get() = role == "LECTURER"
val User.isAdmin
    get() = role == "ADMIN"
