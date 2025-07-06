package com.dailyapps.feature.memo.state

import com.dailyapps.common.utils.DateUtil
import com.dailyapps.entity.Memo
import com.dailyapps.entity.Teacher

/**
 * State classes for the Memo feature
 */
data class MemoState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val memoListState: MemoListState = MemoListState(),
    val memoDetailState: MemoDetailState = MemoDetailState(),
    val addMemoState: AddMemoState = AddMemoState(),
    val updateMemoState: UpdateMemoState = UpdateMemoState(),
    val userId: Long = 0L,
    val token: String = "",
    val role: String = "",
    val isUserLecturer: Boolean = false,
    val isUserNotExist: Boolean = false
)

data class MemoDetailState(
    val memo: Memo? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class MemoListState(
    val memos: List<Memo> = emptyList<Memo>(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val startDate: String = DateUtil.getLastWeekDate(),
    val endDate: String = DateUtil.getCurrentDate(),
    val userId: Long = 0L
)

data class AddMemoState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val title: String = "",
    val active: Boolean = true,
    val status: String = "Aktif",
    val lecturerId: Long = 0L,
    val lecturers: List<Teacher> = emptyList()
)

data class UpdateMemoState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
