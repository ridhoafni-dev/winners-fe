package com.dailyapps.feature.selfreflection.state

import com.dailyapps.common.utils.DateUtil
import com.dailyapps.entity.SelfReflection
import com.dailyapps.entity.Teacher

/**
 * State classes for the Self-Reflection feature
 */
data class SelfReflectionState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isError: Boolean = false,
    val isSuccess: Boolean = false,
    val selfReflectionListState: SelfReflectionListState = SelfReflectionListState(),
    val detail: SelfReflectionDetailState = SelfReflectionDetailState(),
    val add: AddSelfReflectionState = AddSelfReflectionState(),
    val update: UpdateSelfReflectionState = UpdateSelfReflectionState(),
    val userId: Long = 0L,
    val token: String = "",
    val role: String = "",
    val isUserLecturer: Boolean = false,
    val isUserNotExist: Boolean = false
)

data class SelfReflectionDetailState(
    val selfReflection: SelfReflection? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class SelfReflectionListState(
    val selfReflections: List<SelfReflection> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val startDate: String = DateUtil.getLastWeekDate(),
    val endDate: String = DateUtil.getCurrentDate(),
    val userId: Long = 0L
)

data class AddSelfReflectionState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val title: String = "",
    val active: Boolean = true,
    val status: String = "Aktif",
    val lecturerId: Long = 0L,
    val lecturers: List<Teacher> = emptyList()
)

data class UpdateSelfReflectionState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val title: String = "",
    val active: Boolean = true,
    val status: String = "Aktif",
    val lecturerId: Long = 0L,
    val lecturers: List<Teacher> = emptyList()
)
