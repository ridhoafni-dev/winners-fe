package com.dailyapps.data.local.room.entity

import androidx.room.Entity

@Entity(tableName = "teacher", primaryKeys = ["id"])
data class TeacherEntity(
    val nama: String? = null,
    val email: String? = null,
    val id: Int? = null,
    val role: String? = null,
)


