package com.dailyapps.feature.memo

import androidx.lifecycle.viewModelScope
import com.dailyapps.common.utils.DateUtil
import com.dailyapps.common.utils.ViewModelState
import com.dailyapps.domain.usecase.AddMemoCommentUseCase
import com.dailyapps.domain.usecase.AddMemoUseCase
import com.dailyapps.domain.usecase.GetMemoByIdUseCase
import com.dailyapps.domain.usecase.GetMemosByUserIdByDateUseCase
import com.dailyapps.domain.usecase.MasterUseCase
import com.dailyapps.domain.usecase.UpdateMemoUseCase
import com.dailyapps.domain.usecase.UserUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.feature.memo.state.MemoAction
import com.dailyapps.feature.memo.state.MemoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoViewModel @Inject constructor(
    private val getMemosByUserIdByDateUseCase: GetMemosByUserIdByDateUseCase,
    private val getMemoByIdUseCase: GetMemoByIdUseCase,
    private val addMemoUseCase: AddMemoUseCase,
    private val updateMemoUseCase: UpdateMemoUseCase,
    private val addMemoCommentUseCase: AddMemoCommentUseCase,
    private val userUseCase: UserUseCase,
    private val masterUseCase: MasterUseCase
) : ViewModelState<MemoState, MemoAction>(
    initialState = MemoState()
) {

    init {
        getLocal()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getLocal() {
        viewModelScope.launch {
            userUseCase.getUser()
                .distinctUntilChanged()
                .onEach { user ->
                    update {
                        copy(
                            userId = user.id?.toLong() ?: 0L,
                            token = user.token ?: "",
                            role = user.role ?: "",
                            isUserLecturer = (user.role == "lecturer"),
                            isUserNotExist = user.id == null
                        )
                    }
                }
                .flatMapLatest { user ->
                    val token = user.token ?: ""
                    if (token.isNotEmpty()) masterUseCase.getAllTeachersAsFlow()
                    else flowOf(emptyList())
                }
                .collectLatest { teachers ->
                    update { copy(add = add.copy(lecturers = teachers)) }
                }
        }
    }

    override fun handleAction(action: MemoAction) {
        when (action) {
            is MemoAction.GetMemos -> getMemos(
                action.userId,
                action.startDate,
                action.endDate,
                action.token,
                action.lecturer
            )

            is MemoAction.LoadMemo -> getMemoById(
                action.id
            )

            is MemoAction.UpdateDateRange -> {
                update {
                    copy(
                        memoListState = memoListState.copy(
                            startDate = action.startDate,
                            endDate = action.endDate
                        )
                    )
                }
            }

            is MemoAction.AddMemo -> addMemo(
                action.userId,
                action.title,
                action.lecturerId
            )

            is MemoAction.UpdateMemo -> updateMemo(
                action.id,
                action.title,
                action.lecturerId
            )

            is MemoAction.AddComment -> addMemoComment(
                action.memoId,
                action.userId,
                action.comment,
                action.rating
            )

            // New form actions handlers
            is MemoAction.OnGetMemo -> getMemoById(action.id)

            is MemoAction.OnMemoValueChange -> {
                update {
                    copy(
                        add = add.copy(
                            title = action.title,
                            lecturerId = action.lecturerId
                        )
                    )
                }
            }

            is MemoAction.OnSubmitMemo -> {
                val currentState = state.value
                addMemo(
                    currentState.userId,
                    currentState.add.title,
                    currentState.add.lecturerId
                )
            }

            is MemoAction.OnUpdateMemo -> {
                val currentState = state.value
                updateMemo(
                    action.id,
                    currentState.add.title,
                    currentState.add.lecturerId
                )
            }

            is MemoAction.OnResetState -> {
                update {
                    copy(
                        isLoading = false,
                        errorMessage = null,
                        isSuccess = false,
                        add = add.copy(
                            title = "",
                            lecturerId = 0L,
                            isSuccess = false,
                            errorMessage = null,
                            isLoading = false
                        ),
                        detail = detail.copy(
                            memo = null,
                            isLoading = false,
                            errorMessage = null
                        )
                    )
                }
            }

            // Legacy actions for backward compatibility
            is MemoAction.LoadMemos, is MemoAction.FilterByDate -> {
                if (action is MemoAction.FilterByDate) {
                    update {
                        copy(
                            memoListState = memoListState.copy(
                                startDate = action.startDate,
                                endDate = action.endDate
                            )
                        )
                    }
                }
                getMemos(
                    currentState().userId, 
                    currentState().memoListState.startDate, 
                    currentState().memoListState.endDate, 
                    currentState().token
                )
            }
        }
    }

    private fun getMemos(
        userId: Long,
        startDate: String,
        endDate: String,
        token: String,
        isLecturer: Boolean
    ) {
        viewModelScope.launch {
            update { copy(memoListState = memoListState.copy(isLoading = true)) }

            when (val result = getMemosByUserIdByDateUseCase(userId, startDate, endDate, token, isLecturer)) {
                is Resource.Error -> {
                    update {
                        copy(
                            memoListState = memoListState.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        )
                    }
                }
                is Resource.Success -> {
                    update {
                        copy(
                            memoListState = memoListState.copy(
                                isLoading = false,
                                memos = result.data ?: emptyList()
                            )
                        )
                    }
                }
            }
        }
    }

    private fun getMemoById(id: Long) {
        viewModelScope.launch {
            update { copy(detail = detail.copy(isLoading = true)) }

            when (val result = getMemoByIdUseCase(id)) {
                is Resource.Error -> {
                    update {
                        copy(
                            detail = detail.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        )
                    }
                }
                is Resource.Success -> {
                    update {
                        copy(
                            detail = detail.copy(
                                isLoading = false,
                                memo = result.data
                            )
                        )
                    }
                }
            }
        }
    }

    private fun addMemo(
        userId: Long,
        title: String,
        lecturerId: Long
    ) {
        viewModelScope.launch {
            update {
                copy(
                    isLoading = true,
                    errorMessage = null,
                    isSuccess = false
                )
            }

            val today = DateUtil.getCurrentDate()

            when (val result = addMemoUseCase(
                userId,
                title,
                today,
                today,
                true,
                "Aktif",
                lecturerId
            )) {
                is Resource.Error -> {
                    update {
                        copy(
                            isLoading = false,
                            errorMessage = result.message,
                            isSuccess = false
                        )
                    }
                }
                is Resource.Success -> {
                    update {
                        copy(
                            isLoading = false,
                            errorMessage = null,
                            isSuccess = true
                        )
                    }
                }
            }
        }
    }

    private fun updateMemo(
        id: Long,
        title: String,
        lecturerId: Long
    ) {
        viewModelScope.launch {
            update {
                copy(
                    isLoading = true,
                    errorMessage = null,
                    isSuccess = false
                )
            }

            when (val result = updateMemoUseCase(id, title, lecturerId)) {
                is Resource.Error -> {
                    update {
                        copy(
                            isLoading = false,
                            errorMessage = result.message,
                            isSuccess = false
                        )
                    }
                }
                is Resource.Success -> {
                    update {
                        copy(
                            isLoading = false,
                            errorMessage = null,
                            isSuccess = true
                        )
                    }
                }
            }
        }
    }

    private fun addMemoComment(
        memoId: Long,
        userId: Long,
        comment: String,
        rating: Int
    ) {
        viewModelScope.launch {
            update { copy(isLoading = true) }

            when (val result = addMemoCommentUseCase(memoId, userId, comment, rating)) {
                is Resource.Error -> {
                    update {
                        copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                is Resource.Success -> {
                    update {
                        copy(
                            isLoading = false
                        )
                    }
                    // Reload the memo to get updated comments
                    getMemoById(memoId)
                }
            }
        }
    }
}
