package com.dailyapps.feature.memo

import androidx.lifecycle.viewModelScope
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
                    update { copy(addMemoState = addMemoState.copy(lecturers = teachers)) }
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

            is MemoAction.AddComment -> addComment(
                action.memoId,
                action.userId,
                action.comment,
                action.rating
            )

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
        lecturer: Boolean = false
    ) {
        viewModelScope.launch {
            update { 
                copy(
                    isLoading = true,
                    memoListState = memoListState.copy(isLoading = true)
                )
            }

            getMemosByUserIdByDateUseCase(
                GetMemosByUserIdByDateUseCase.Params(
                    userId = userId,
                    startDate = startDate,
                    endDate = endDate,
                    lecturer = lecturer,
                    token = token
                )
            ).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        update {
                            copy(
                                isLoading = false,
                                errorMessage = null,
                                memoListState = memoListState.copy(
                                    memos = result.data,
                                    isLoading = false,
                                    errorMessage = null
                                )
                            )
                        }
                    }
                    is Resource.Error -> {
                        update {
                            copy(
                                isLoading = false,
                                errorMessage = result.msg,
                                memoListState = memoListState.copy(
                                    isLoading = false,
                                    errorMessage = result.msg
                                )
                            )
                        }
                    }
                    is Resource.Loading -> {
                        update {
                            copy(
                                isLoading = true,
                                memoListState = memoListState.copy(
                                    isLoading = true
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getMemoById(id: Long) {
        viewModelScope.launch {
            update {
                copy(
                    memoDetailState = memoDetailState.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                )
            }

            getMemoByIdUseCase(
                GetMemoByIdUseCase.Params(
                    id = id,
                    token = currentState().token
                )
            ).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        update {
                            copy(
                                memoDetailState = memoDetailState.copy(
                                    memo = result.data,
                                    isLoading = false,
                                    errorMessage = null
                                )
                            )
                        }
                    }
                    is Resource.Error -> {
                        update {
                            copy(
                                memoDetailState = memoDetailState.copy(
                                    isLoading = false,
                                    errorMessage = result.msg
                                )
                            )
                        }
                    }
                    is Resource.Loading -> {
                        update {
                            copy(
                                memoDetailState = memoDetailState.copy(
                                    isLoading = true
                                )
                            )
                        }
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
                    addMemoState = addMemoState.copy(
                        isLoading = true,
                        errorMessage = null,
                        isSuccess = false
                    )
                )
            }

            addMemoUseCase(
                AddMemoUseCase.Params(
                    userId = userId,
                    title = title,
                    lecturerId = lecturerId,
                    token = currentState().token
                )
            ).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        update {
                            copy(
                                addMemoState = addMemoState.copy(
                                    isLoading = false,
                                    errorMessage = null,
                                    isSuccess = true
                                )
                            )
                        }
                        // Refresh the memo list after adding
                        getMemos(
                            currentState().userId,
                            currentState().memoListState.startDate,
                            currentState().memoListState.endDate,
                            currentState().token
                        )
                    }
                    is Resource.Error -> {
                        update {
                            copy(
                                addMemoState = addMemoState.copy(
                                    isLoading = false,
                                    errorMessage = result.msg,
                                    isSuccess = false
                                )
                            )
                        }
                    }
                    is Resource.Loading -> {
                        update {
                            copy(
                                addMemoState = addMemoState.copy(
                                    isLoading = true
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun updateMemo(
        id: Long,
        title: String,
        lecturerId: Long,
    ) {
        viewModelScope.launch {
            update {
                copy(
                    updateMemoState = updateMemoState.copy(
                        isLoading = true,
                        errorMessage = null,
                        isSuccess = false
                    )
                )
            }

            updateMemoUseCase(
                UpdateMemoUseCase.Params(
                    id = id,
                    title = title,
                    token = currentState().token,
                    userId = currentState().userId,
                    lecturerId = lecturerId
                )
            ).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        update {
                            copy(
                                updateMemoState = updateMemoState.copy(
                                    isLoading = false,
                                    errorMessage = null,
                                    isSuccess = true
                                )
                            )
                        }
                        // Refresh the memo list after updating
                        getMemos(
                            currentState().userId,
                            currentState().memoListState.startDate,
                            currentState().memoListState.endDate,
                            currentState().token
                        )
                        // Also refresh the detail view if we're looking at this memo
                        currentState().memoDetailState.memo?.let { memo ->
                            if (memo.id?.toLong() == id) {
                                getMemoById(id)
                            }
                        }
                    }
                    is Resource.Error -> {
                        update {
                            copy(
                                updateMemoState = updateMemoState.copy(
                                    isLoading = false,
                                    errorMessage = result.msg,
                                    isSuccess = false
                                )
                            )
                        }
                    }
                    is Resource.Loading -> {
                        update {
                            copy(
                                updateMemoState = updateMemoState.copy(
                                    isLoading = true
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun addComment(
        memoId: Long,
        userId: Long,
        comment: String,
        rating: Int
    ) {
        viewModelScope.launch {
            addMemoCommentUseCase(
                AddMemoCommentUseCase.Params(
                    id = memoId,
                    userId = userId,
                    comment = comment,
                    rating = rating,
                    token = currentState().token
                )
            ).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        // Refresh the memo detail to see the new comment
                        getMemoById(memoId)
                    }
                    is Resource.Error -> {
                        // Update error state if needed
                    }
                    is Resource.Loading -> {
                        // Update loading state if needed
                    }
                }
            }
        }
    }
}
