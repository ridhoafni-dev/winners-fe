package com.dailyapps.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "user",
    primaryKeys = ["id"]
)
data class UserEntity(
    val id: Int? = null,
    val role: String? = null,
    val address: String? = null,
    val nim: String? = null,
    val name: String? = null,
    val stase: String? = null,
    val endSchoolYear: Int? = null,
    val email: String? = null,
    val startSchoolYear: Int? = null,
    val token: String? = null
)