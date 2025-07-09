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
                            isUserLecturer = (user.role == "LECTURER"),
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

            is MemoAction.OnSubmitMemo -> onSubmitMemo()

            is MemoAction.OnUpdateMemo -> onUpdateMemo(action.id)

            is MemoAction.OnResetState -> {
                update {
                    copy(
                        isLoading = false,
                        errorMessage = null,
                        isError = false,
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

    private fun onUpdateMemo(memoId: Long) {
        viewModelScope.launch {
           updateMemoUseCase(
                UpdateMemoUseCase.Params(
                    token = currentState().token,
                    id = memoId,
                    title = currentState().add.title,
                    lecturerId = currentState().add.lecturerId,
                    userId = currentState().userId
                )
            ).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        update {
                            copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = result.msg,
                                isSuccess = false
                            )
                        }
                    }

                    is Resource.Success -> {
                        update {
                            copy(
                                isLoading = false,
                                isError = false,
                                errorMessage = null,
                                isSuccess = true
                            )
                        }
                    }

                    Resource.Loading -> update {
                        copy(
                            isLoading = true,
                            isError = false,
                            errorMessage = null
                        )
                    }
                }
            }

        }
    }

    private fun onSubmitMemo() {
        viewModelScope.launch {
           addMemoUseCase(
               AddMemoUseCase.Params(
                   token = currentState().token,
                   userId = currentState().userId,
                   title = currentState().add.title,
                   lecturerId = currentState().add.lecturerId
                )
            ).collectLatest { result ->
               when (result) {
                   is Resource.Error -> {
                       update {
                           copy(
                               isLoading = false,
                               isError = true,
                               errorMessage = result.msg,
                               isSuccess = false
                           )
                       }
                   }

                   is Resource.Success -> {
                       update {
                           copy(
                               isLoading = false,
                               isError = false,
                               errorMessage = null,
                               isSuccess = true
                           )
                       }
                   }

                   Resource.Loading -> update {
                       copy(
                           isLoading = true,
                           isError = false,
                           errorMessage = null
                       )
                   }
               }
           }
        }
    }

    private fun getMemos(
        userId: Long,
        startDate: String,
        endDate: String,
        token: String,
        isLecturer: Boolean = false
    ) {
        viewModelScope.launch {

            getMemosByUserIdByDateUseCase(
                GetMemosByUserIdByDateUseCase.Params(
                    userId = userId,
                    startDate = startDate,
                    endDate = endDate,
                    token = token,
                    lecturer = isLecturer
                )
            ).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        update {
                            copy(
                                memoListState = memoListState.copy(
                                    isLoading = false,
                                    errorMessage = result.msg
                                )
                            )
                        }
                    }

                    is Resource.Success -> {
                        update {
                            copy(
                                memoListState = memoListState.copy(
                                    isLoading = false,
                                    memos = result.data
                                )
                            )
                        }
                    }

                    Resource.Loading -> update { copy(memoListState = memoListState.copy(isLoading = true)) }

                }
            }
        }
    }

    private fun getMemoById(id: Long) {
        viewModelScope.launch {

            getMemoByIdUseCase(GetMemoByIdUseCase.Params(
                token = currentState().token,
                id = id
            )).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        update {
                            copy(
                                detail = detail.copy(
                                    isLoading = false,
                                    errorMessage = result.msg
                                ),
                                isLoading = false,
                                isError = true,
                                errorMessage = result.msg
                            )
                        }
                    }

                    is Resource.Success -> {
                        val memo = result.data
                        update {
                            copy(
                                detail = detail.copy(
                                    isLoading = false,
                                    memo = memo
                                ),
                                isLoading = false,
                                add = add.copy(
                                    title = memo?.title ?: "",
                                    lecturerId = memo?.memoLecturer?.userId?.toLong() ?: 0L
                                )
                            )
                        }
                    }

                    Resource.Loading -> update {
                        copy(
                            detail = detail.copy(isLoading = true),
                            isLoading = true
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
            addMemoCommentUseCase(AddMemoCommentUseCase.Params(
                token = currentState().token, id = memoId, userId = userId, comment = comment, rating = rating
            )).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        update {
                            copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = result.msg
                            )
                        }
                    }

                    is Resource.Success -> {
                        update {
                            copy(
                                isLoading = false,
                                isError = false,
                                isSuccess = true
                            )
                        }
                        // Reload the memo to get updated comments
                        getMemoById(memoId)
                    }

                    Resource.Loading -> update { copy(isLoading = true) }
                }
            }
        }
    }
}
