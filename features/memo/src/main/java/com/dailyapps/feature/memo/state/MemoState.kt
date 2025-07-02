package com.dailyapps.feature.memo.state

import com.dailyapps.entity.Memo

/**
 * State classes for the Memo feature
 */
data class MemoState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val memoListState: MemoListState = MemoListState(),
    val memoDetailState: MemoDetailState = MemoDetailState(),
    val addMemoState: AddMemoState = AddMemoState(),
    val updateMemoState: UpdateMemoState = UpdateMemoState()
)

data class MemoDetailState(
    val memo: Memo? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class MemoListState(
    val memos: List<Memo> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val startDate: String = "",
    val endDate: String = ""
)

data class AddMemoState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

data class UpdateMemoState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
