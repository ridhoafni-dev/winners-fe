package com.dailyapps.data.mapper

import com.dailyapps.data.local.room.entity.TeacherEntity
import com.dailyapps.entity.Teacher

fun TeacherEntity.toTeacher(): Teacher {
    return Teacher(
        id = id,
        name = nama,
        email = email,
        role = role
    )
}