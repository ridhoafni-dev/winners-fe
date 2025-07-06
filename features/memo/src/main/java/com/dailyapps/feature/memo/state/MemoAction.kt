package com.dailyapps.feature.memo.state
/**
 * Actions that can be performed in the Memo feature
 */
sealed class MemoAction {
    object LoadMemos : MemoAction()
    data class LoadMemo(val id: Long) : MemoAction()
    data class FilterByDate(val startDate: String, val endDate: String) : MemoAction()
    data class GetMemos(
        val userId: Long,
        val startDate: String,
        val endDate: String,
        val token: String,
        val lecturer: Boolean = false
    ) : MemoAction()
    data class UpdateDateRange(val startDate: String, val endDate: String) : MemoAction()
    data class AddMemo(
        val userId: Long,
        val title: String,
        val startDate: String,
        val endDate: String,
        val active: Boolean,
        val status: String,
        val lecturerId: Long
    ) : MemoAction()
    data class UpdateMemo(
        val id: Long,
        val title: String,
        val lecturerId: Long,
    ) : MemoAction()
    data class AddComment(
        val memoId: Long,
        val userId: Long,
        val comment: String,
        val rating: Int
    ) : MemoAction()
}
