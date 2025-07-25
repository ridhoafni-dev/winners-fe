package com.dailyapps.feature.selfreflection

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
import com.dailyapps.feature.selfreflection.state.AddSelfReflectionState
import com.dailyapps.feature.selfreflection.state.SelfReflectionAction
import com.dailyapps.feature.selfreflection.state.SelfReflectionState
import com.dailyapps.feature.selfreflection.state.UpdateSelfReflectionState
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
                    update { copy(add = add.copy(lecturers = teachers), update = update.copy(lecturers = teachers)) }
                }
        }
    }

    override fun handleAction(action: SelfReflectionAction) {
        when (action) {
            is SelfReflectionAction.LoadSelfReflections -> {
                getSelfReflectionsList()
            }
            is SelfReflectionAction.LoadSelfReflection -> {
                getSelfReflection(action.id)
            }
            is SelfReflectionAction.FilterByDate -> {
                filterByDate(action.startDate, action.endDate)
            }
            is SelfReflectionAction.GetSelfReflections -> {
                getSelfReflections(
                    action.userId,
                    action.startDate,
                    action.endDate,
                    action.token,
                    action.lecturer
                )
            }
            is SelfReflectionAction.UpdateDateRange -> {
                updateDateRange(action.startDate, action.endDate)
            }
            is SelfReflectionAction.AddComment -> {
                addComment(
                    action.reflectionId,
                    action.userId,
                    action.rating,
                    action.comment
                )
            }
            is SelfReflectionAction.OnGetSelfReflection -> {
                getSelfReflection(action.id, action.token)
            }
            is SelfReflectionAction.OnSelfReflectionValueChange -> {
                onSelfReflectionValueChange(action.title, action.lecturerId)
            }
            is SelfReflectionAction.OnSubmitSelfReflection -> {
                onSubmitSelfReflection()
            }
            is SelfReflectionAction.OnUpdateSelfReflection -> {
                onUpdateSelfReflection(action.id)
            }
            is SelfReflectionAction.OnResetState -> {
                resetState()
            }
        }
    }

    private fun getSelfReflectionsList() {
        viewModelScope.launch {
            update {
                copy(
                    selfReflectionListState = selfReflectionListState.copy(
                        isLoading = true
                    )
                )
            }

            val params = GetSelfReflectionsByUserIdByDateUseCase.Params(
                token = state.value.token,
                userId = state.value.userId,
                startDate = state.value.selfReflectionListState.startDate,
                endDate = state.value.selfReflectionListState.endDate,
                lecturer = state.value.isUserLecturer
            )

            getSelfReflectionsByUserIdByDateUseCase(params)
                .collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            update {
                                copy(
                                    selfReflectionListState = selfReflectionListState.copy(
                                        selfReflections = resource.data,
                                        isLoading = false,
                                        errorMessage = null
                                    )
                                )
                            }
                        }
                        is Resource.Error -> {
                            update {
                                copy(
                                    selfReflectionListState = selfReflectionListState.copy(
                                        isLoading = false,
                                        errorMessage = resource.msg
                                    )
                                )
                            }
                        }
                        is Resource.Loading -> {
                            update {
                                copy(
                                    selfReflectionListState = selfReflectionListState.copy(
                                        isLoading = true,
                                        errorMessage = null
                                    )
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun getSelfReflection(id: Long, token: String = state.value.token) {
        viewModelScope.launch {
            update {
                copy(
                    detail = detail.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                )
            }

            val params = GetSelfReflectionByIdUseCase.Params(
                token = token,
                id = id
            )

            getSelfReflectionByIdUseCase(params).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        val selfReflection = resource.data
                        update {
                            copy(
                                detail = detail.copy(
                                    selfReflection = selfReflection,
                                    isLoading = false,
                                    errorMessage = null
                                ),
                                update = update.copy(
                                    title = selfReflection.title ?: "",
                                    active = selfReflection.active ?: true,
                                    status = if (selfReflection.active == true) "Aktif" else "Tidak Aktif",
                                    lecturerId = selfReflection.selfReflectionLecturer?.userId ?: 0L
                                )
                            )
                        }
                    }
                    is Resource.Error -> {
                        update {
                            copy(
                                detail = detail.copy(
                                    isLoading = false,
                                    errorMessage = resource.msg
                                )
                            )
                        }
                    }
                    is Resource.Loading -> {
                        update {
                            copy(
                                detail = detail.copy(
                                    isLoading = true,
                                    errorMessage = null
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun filterByDate(startDate: String, endDate: String) {
        viewModelScope.launch {
            update {
                copy(
                    selfReflectionListState = selfReflectionListState.copy(
                        startDate = startDate,
                        endDate = endDate
                    )
                )
            }
            getSelfReflectionsList()
        }
    }

    private fun getSelfReflections(userId: Long, startDate: String, endDate: String, token: String, lecturer: Boolean) {
        viewModelScope.launch {
            update {
                copy(
                    selfReflectionListState = selfReflectionListState.copy(
                        isLoading = true
                    )
                )
            }

            val params = GetSelfReflectionsByUserIdByDateUseCase.Params(
                token = token,
                userId = userId,
                startDate = startDate,
                endDate = endDate,
                lecturer = lecturer
            )

            getSelfReflectionsByUserIdByDateUseCase(params)
                .collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            update {
                                copy(
                                    selfReflectionListState = selfReflectionListState.copy(
                                        selfReflections = resource.data,
                                        isLoading = false,
                                        errorMessage = null
                                    )
                                )
                            }
                        }
                        is Resource.Error -> {
                            update {
                                copy(
                                    selfReflectionListState = selfReflectionListState.copy(
                                        isLoading = false,
                                        errorMessage = resource.msg
                                    )
                                )
                            }
                        }
                        is Resource.Loading -> {
                            update {
                                copy(
                                    selfReflectionListState = selfReflectionListState.copy(
                                        isLoading = true,
                                        errorMessage = null
                                    )
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun updateDateRange(startDate: String, endDate: String) {
        update {
            copy(
                selfReflectionListState = selfReflectionListState.copy(
                    startDate = startDate,
                    endDate = endDate
                )
            )
        }
    }

    private fun addComment(reflectionId: Long, userId: Long, rating: Int, comment: String) {
        viewModelScope.launch {
            update {
                copy(
                    isLoading = true
                )
            }

            val params = AddSelfReflectionCommentUseCase.Params(
                token = state.value.token,
                reflectionId = reflectionId,
                userId = userId,
                rating = rating,
                comment = comment
            )

            addSelfReflectionCommentUseCase(params).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        update {
                            copy(
                                isLoading = false,
                                isSuccess = true
                            )
                        }
                    }
                    is Resource.Error -> {
                        update {
                            copy(
                                isLoading = false,
                                errorMessage = resource.msg
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun onSelfReflectionValueChange(title: String, lecturerId: Long) {
        update {
            copy(
                add = add.copy(
                    title = title,
                    lecturerId = lecturerId
                ),
                update = update.copy(
                    title = title,
                    lecturerId = lecturerId
                )
            )
        }
    }

    private fun onSubmitSelfReflection() {
        viewModelScope.launch {
            update {
                copy(
                    add = add.copy(
                        isLoading = true,
                        isSuccess = false,
                        errorMessage = null
                    )
                )
            }

            val params = AddSelfReflectionUseCase.Params(
                token = state.value.token,
                userId = state.value.userId,
                title = state.value.add.title,
                lecturerId = state.value.add.lecturerId
            )

            addSelfReflectionUseCase(params).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        update {
                            copy(
                                add = add.copy(
                                    isLoading = false,
                                    isSuccess = true,
                                    errorMessage = null
                                )
                            )
                        }
                    }
                    is Resource.Error -> {
                        update {
                            copy(
                                add = add.copy(
                                    isLoading = false,
                                    isSuccess = false,
                                    errorMessage = resource.msg
                                )
                            )
                        }
                    }
                    is Resource.Loading -> {
                        update {
                            copy(
                                add = add.copy(
                                    isLoading = true,
                                    isSuccess = false,
                                    errorMessage = null
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun onUpdateSelfReflection(id: Long) {
        viewModelScope.launch {
            update {
                copy(
                    update = update.copy(
                        isLoading = true,
                        isSuccess = false,
                        errorMessage = null
                    )
                )
            }

            val params = UpdateSelfReflectionUseCase.Params(
                token = state.value.token,
                id = id,
                userId = state.value.userId,
                title = state.value.update.title,
                lecturerId = state.value.update.lecturerId
            )

            updateSelfReflectionUseCase(params).collectLatest { resource ->
                when (resource) {
                    is Resource.Success -> {
                        update {
                            copy(
                                update = update.copy(
                                    isLoading = false,
                                    isSuccess = true,
                                    errorMessage = null
                                )
                            )
                            }
                        }
                    is Resource.Error -> {
                        update {
                            copy(
                                update = update.copy(
                                    isLoading = false,
                                    isSuccess = false,
                                    errorMessage = resource.msg
                                )
                            )
                        }
                    }
                    is Resource.Loading -> {
                        update {
                            copy(
                                update = update.copy(
                                    isLoading = true,
                                    isSuccess = false,
                                    errorMessage = null
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun resetState() {
        update {
            copy(
                isLoading = false,
                errorMessage = null,
                isError = false,
                isSuccess = false,
                add = AddSelfReflectionState(),
                update = UpdateSelfReflectionState()
            )
        }
    }

}
