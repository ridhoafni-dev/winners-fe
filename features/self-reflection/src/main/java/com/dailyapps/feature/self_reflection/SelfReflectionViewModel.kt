package com.dailyapps.feature.self_reflection

import androidx.lifecycle.viewModelScope
import com.dailyapps.common.utils.DateUtil
import com.dailyapps.common.utils.ViewModelState
import com.dailyapps.domain.usecase.AddSelfReflectionCommentUseCase
import com.dailyapps.domain.usecase.AddSelfReflectionUseCase
import com.dailyapps.domain.usecase.GetSelfReflectionByIdUseCase
import com.dailyapps.domain.usecase.GetSelfReflectionsByUserIdByDateUseCase
import com.dailyapps.domain.usecase.MasterUseCase
import com.dailyapps.domain.usecase.UpdateSelfReflectionUseCase
import com.dailyapps.domain.usecase.UserUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.feature.selfreflection.state.SelfReflectionAction
import com.dailyapps.feature.selfreflection.state.SelfReflectionState
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
class SelfReflectionViewModel @Inject constructor(
    private val getSelfReflectionsByUserIdByDateUseCase: GetSelfReflectionsByUserIdByDateUseCase,
    private val getSelfReflectionByIdUseCase: GetSelfReflectionByIdUseCase,
    private val addSelfReflectionUseCase: AddSelfReflectionUseCase,
    private val updateSelfReflectionUseCase: UpdateSelfReflectionUseCase,
    private val addSelfReflectionCommentUseCase: AddSelfReflectionCommentUseCase,
    private val userUseCase: UserUseCase,
    private val masterUseCase: MasterUseCase
) : ViewModelState<SelfReflectionState, SelfReflectionAction>(
    initialState = SelfReflectionState()
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

    override fun handleAction(action: SelfReflectionAction) {
        when (action) {
            is SelfReflectionAction.GetSelfReflections -> getSelfReflections(
                action.userId,
                action.startDate,
                action.endDate,
                action.token,
                action.lecturer
            )

            is SelfReflectionAction.LoadSelfReflection -> getSelfReflectionById(
                action.id
            )

            is SelfReflectionAction.UpdateDateRange -> {
                update {
                    copy(
                        selfReflectionListState = selfReflectionListState.copy(
                            startDate = action.startDate,
                            endDate = action.endDate
                        )
                    )
                }
            }

            is SelfReflectionAction.AddComment -> addSelfReflectionComment(
                action.reflectionId,
                action.userId,
                action.comment,
                action.rating
            )

            // Form actions handlers
            is SelfReflectionAction.OnGetSelfReflection -> getSelfReflectionById(action.id)

            is SelfReflectionAction.OnSelfReflectionValueChange -> {
                update {
                    copy(
                        add = add.copy(
                            title = action.title,
                            lecturerId = action.lecturerId
                        )
                    )
                }
            }

            is SelfReflectionAction.OnSubmitSelfReflection -> onSubmitSelfReflection()

            is SelfReflectionAction.OnUpdateSelfReflection -> onUpdateSelfReflection(action.id)

            is SelfReflectionAction.OnResetState -> {
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
                            selfReflection = null,
                            isLoading = false,
                            errorMessage = null
                        )
                    )
                }
            }

            // Legacy actions for backward compatibility
            is SelfReflectionAction.LoadSelfReflections, is SelfReflectionAction.FilterByDate -> {
                if (action is SelfReflectionAction.FilterByDate) {
                    update {
                        copy(
                            selfReflectionListState = selfReflectionListState.copy(
                                startDate = action.startDate,
                                endDate = action.endDate
                            )
                        )
                    }
                }
                getSelfReflections(
                    currentState().userId,
                    currentState().selfReflectionListState.startDate,
                    currentState().selfReflectionListState.endDate,
                    currentState().token
                )
            }
        }
    }

    private fun getSelfReflections(
        userId: Long,
        startDate: String,
        endDate: String,
        token: String,
        lecturer: Boolean = false
    ) {
        viewModelScope.launch {
            update { copy(selfReflectionListState = selfReflectionListState.copy(isLoading = true)) }

            getSelfReflectionsByUserIdByDateUseCase(
                GetSelfReflectionsByUserIdByDateUseCase.Params(
                    token = token,
                    userId = userId,
                    startDate = startDate,
                    endDate = endDate,
                    lecturer = lecturer
                )
            ).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        update {
                            copy(
                                selfReflectionListState = selfReflectionListState.copy(
                                    isLoading = false,
                                    errorMessage = result.msg
                                )
                            )
                        }
                    }
                    is Resource.Success -> {
                        update {
                            copy(
                                selfReflectionListState = selfReflectionListState.copy(
                                    isLoading = false,
                                    selfReflections = result.data ?: emptyList()
                                )
                            )
                        }
                    }
                    Resource.Loading -> {
                        update {
                            copy(
                                selfReflectionListState = selfReflectionListState.copy(
                                    isLoading = true
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getSelfReflectionById(id: Long) {
        viewModelScope.launch {
            update { copy(detail = detail.copy(isLoading = true)) }

            getSelfReflectionByIdUseCase(
                GetSelfReflectionByIdUseCase.Params(
                    token = currentState().token,
                    id = id
                )
            ).collectLatest { result ->
                when (result) {
                    is Resource.Error -> {
                        update {
                            copy(
                                detail = detail.copy(
                                    isLoading = false,
                                    errorMessage = result.msg
                                )
                            )
                        }
                    }
                    is Resource.Success -> {
                        update {
                            copy(
                                detail = detail.copy(
                                    isLoading = false,
                                    selfReflection = result.data
                                )
                            )
                        }
                    }
                    Resource.Loading -> {
                        update { copy(detail = detail.copy(isLoading = true)) }
                    }
                }
            }
        }
    }

    private fun onSubmitSelfReflection() {
        viewModelScope.launch {
            update {
                copy(
                    isLoading = true,
                    isError = false,
                    errorMessage = null,
                    isSuccess = false
                )
            }

            addSelfReflectionUseCase(
                AddSelfReflectionUseCase.Params(
                    token = currentState().token,
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
                            errorMessage = null,
                            isSuccess = false
                        )
                    }
                }
            }
        }
    }

    private fun onUpdateSelfReflection(selfReflectionId: Long) {
        viewModelScope.launch {
           updateSelfReflectionUseCase(
                UpdateSelfReflectionUseCase.Params(
                    token = currentState().token,
                    id = selfReflectionId,
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
                            isLoading = true
                        )
                    }
                }
            }
        }
    }

    private fun addSelfReflectionComment(
        selfReflectionId: Long,
        userId: Long,
        comment: String,
        rating: Int
    ) {
//        viewModelScope.launch {
//            update { copy(isLoading = true) }
//
//            addSelfReflectionCommentUseCase(
//                AddSelfReflectionCommentUseCase.Params(
//                    token = currentState().token,
//                    reflectionId = selfReflectionId,
//                    userId = userId,
//                    comment = comment,
//                    rating = rating
//                )
//            ).collectLatest { result ->
//                when (result) {
//                    is Resource.Error -> {
//                        update {
//                            copy(
//                                isLoading = false,
//                                isError = true,
//                                errorMessage = result.msg
//                            )
//                        }
//                    }
//                    is Resource.Success -> {
//                        // After adding comment successfully, refresh the self-reflection details
//                        getSelfReflectionById(selfReflectionId)
//                        update {
//                            copy(
//                                isLoading = false,
//                                isError = false
//                            )
//                        }
//                    }
//                    Resource.Loading -> {
//                        update { copy(isLoading = true) }
//                    }
//                }
//            }
//        }
    }
}
