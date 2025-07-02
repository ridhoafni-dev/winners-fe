package com.dailyapps.feature.memo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dailyapps.domain.usecase.AddMemoCommentUseCase
import com.dailyapps.domain.usecase.AddMemoUseCase
import com.dailyapps.domain.usecase.GetMemoByIdUseCase
import com.dailyapps.domain.usecase.GetMemosByUserIdByDateUseCase
import com.dailyapps.domain.usecase.UpdateMemoUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.feature.memo.state.MemoAction
import com.dailyapps.feature.memo.state.MemoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoViewModel @Inject constructor(
    private val getMemosByUserIdByDateUseCase: GetMemosByUserIdByDateUseCase,
    private val getMemoByIdUseCase: GetMemoByIdUseCase,
    private val addMemoUseCase: AddMemoUseCase,
    private val updateMemoUseCase: UpdateMemoUseCase,
    private val addMemoCommentUseCase: AddMemoCommentUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MemoState())
    val state: StateFlow<MemoState> = _state.asStateFlow()

    fun dispatch(action: MemoAction) {
        when (action) {
            is MemoAction.LoadMemos -> loadMemos()
            is MemoAction.LoadMemo -> loadMemo(action.id)
            is MemoAction.FilterByDate -> filterByDate(action.startDate, action.endDate)
            is MemoAction.AddMemo -> addMemo(
                userId = action.userId,
                title = action.title,
                startDate = action.startDate,
                endDate = action.endDate,
                active = action.active,
                status = action.status,
                lecturerId = action.lecturerId
            )
            is MemoAction.UpdateMemo -> updateMemo(
                id = action.id,
                title = action.title,
                startDate = action.startDate,
                endDate = action.endDate,
                active = action.active,
                status = action.status
            )
            is MemoAction.AddComment -> addComment(
                memoId = action.memoId,
                userId = action.userId,
                comment = action.comment,
                rating = action.rating
            )
        }
    }

    private fun loadMemos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val startDate = _state.value.memoListState.startDate
            val endDate = _state.value.memoListState.endDate

            // Default values, these would typically come from a user session or preference
            val userId = 1L
            val lecturer = false

            try {
                getMemosByUserIdByDateUseCase(
                    GetMemosByUserIdByDateUseCase.Params(
                        userId = userId,
                        startDate = startDate,
                        endDate = endDate,
                        lecturer = lecturer
                    )
                ).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = null,
                                    memoListState = it.memoListState.copy(
                                        memos = result.data ?: emptyList(),
                                        isLoading = false,
                                        errorMessage = null
                                    )
                                )
                            }
                        }
                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = result.message,
                                    memoListState = it.memoListState.copy(
                                        isLoading = false,
                                        errorMessage = result.message
                                    )
                                )
                            }
                        }
                        is Resource.Loading -> {
                            _state.update {
                                it.copy(
                                    isLoading = true,
                                    memoListState = it.memoListState.copy(
                                        isLoading = true
                                    )
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message,
                        memoListState = it.memoListState.copy(
                            isLoading = false,
                            errorMessage = e.message
                        )
                    )
                }
            }
        }
    }

    private fun loadMemo(id: Long) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    memoDetailState = it.memoDetailState.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                )
            }

            try {
                getMemoByIdUseCase(
                    GetMemoByIdUseCase.Params(id = id)
                ).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.update {
                                it.copy(
                                    memoDetailState = it.memoDetailState.copy(
                                        memo = result.data,
                                        isLoading = false,
                                        errorMessage = null
                                    )
                                )
                            }
                        }
                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    memoDetailState = it.memoDetailState.copy(
                                        isLoading = false,
                                        errorMessage = result.message
                                    )
                                )
                            }
                        }
                        is Resource.Loading -> {
                            _state.update {
                                it.copy(
                                    memoDetailState = it.memoDetailState.copy(
                                        isLoading = true
                                    )
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        memoDetailState = it.memoDetailState.copy(
                            isLoading = false,
                            errorMessage = e.message
                        )
                    )
                }
            }
        }
    }

    private fun filterByDate(startDate: String, endDate: String) {
        _state.update {
            it.copy(
                memoListState = it.memoListState.copy(
                    startDate = startDate,
                    endDate = endDate
                )
            )
        }
        loadMemos()
    }

    private fun addMemo(
        userId: Long,
        title: String,
        startDate: String,
        endDate: String,
        active: Boolean,
        status: String,
        lecturerId: Long
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    addMemoState = it.addMemoState.copy(
                        isLoading = true,
                        errorMessage = null,
                        isSuccess = false
                    )
                )
            }

            try {
                addMemoUseCase(
                    AddMemoUseCase.Params(
                        userId = userId,
                        title = title,
                        startDate = startDate,
                        endDate = endDate,
                        active = active,
                        status = status,
                        lecturerId = lecturerId
                    )
                ).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.update {
                                it.copy(
                                    addMemoState = it.addMemoState.copy(
                                        isLoading = false,
                                        errorMessage = null,
                                        isSuccess = true
                                    )
                                )
                            }
                            // Refresh the memo list after adding
                            loadMemos()
                        }
                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    addMemoState = it.addMemoState.copy(
                                        isLoading = false,
                                        errorMessage = result.message,
                                        isSuccess = false
                                    )
                                )
                            }
                        }
                        is Resource.Loading -> {
                            _state.update {
                                it.copy(
                                    addMemoState = it.addMemoState.copy(
                                        isLoading = true,
                                        errorMessage = null,
                                        isSuccess = false
                                    )
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        addMemoState = it.addMemoState.copy(
                            isLoading = false,
                            errorMessage = e.message,
                            isSuccess = false
                        )
                    )
                }
            }
        }
    }

    private fun updateMemo(
        id: Long,
        title: String,
        startDate: String,
        endDate: String,
        active: Boolean,
        status: String
    ) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    updateMemoState = it.updateMemoState.copy(
                        isLoading = true,
                        errorMessage = null,
                        isSuccess = false
                    )
                )
            }

            try {
                updateMemoUseCase(
                    UpdateMemoUseCase.Params(
                        id = id,
                        title = title,
                        startDate = startDate,
                        endDate = endDate,
                        active = active,
                        status = status
                    )
                ).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.update {
                                it.copy(
                                    updateMemoState = it.updateMemoState.copy(
                                        isLoading = false,
                                        errorMessage = null,
                                        isSuccess = true
                                    )
                                )
                            }
                            // Refresh the memo list after updating
                            loadMemos()
                            // Also refresh the detail view if we're looking at this memo
                            _state.value.memoDetailState.memo?.let { memo ->
                                if (memo.id == id) {
                                    loadMemo(id)
                                }
                            }
                        }
                        is Resource.Error -> {
                            _state.update {
                                it.copy(
                                    updateMemoState = it.updateMemoState.copy(
                                        isLoading = false,
                                        errorMessage = result.message,
                                        isSuccess = false
                                    )
                                )
                            }
                        }
                        is Resource.Loading -> {
                            _state.update {
                                it.copy(
                                    updateMemoState = it.updateMemoState.copy(
                                        isLoading = true,
                                        errorMessage = null,
                                        isSuccess = false
                                    )
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        updateMemoState = it.updateMemoState.copy(
                            isLoading = false,
                            errorMessage = e.message,
                            isSuccess = false
                        )
                    )
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
            try {
                addMemoCommentUseCase(
                    AddMemoCommentUseCase.Params(
                        memoId = memoId,
                        userId = userId,
                        comment = comment,
                        rating = rating
                    )
                ).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            // Refresh the memo detail to see the new comment
                            loadMemo(memoId)
                        }
                        is Resource.Error -> {
                            // Handle error
                        }
                        is Resource.Loading -> {
                            // Handle loading state
                        }
                    }
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }
}
