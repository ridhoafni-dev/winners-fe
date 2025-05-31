package com.dailyapps.feature.activty_plan

import androidx.lifecycle.viewModelScope
import com.dailyapps.common.utils.ViewModelState
import com.dailyapps.domain.usecase.AddActivityPlanCommentUseCase
import com.dailyapps.domain.usecase.AddActivityPlanUseCase
import com.dailyapps.domain.usecase.GetActivityPlanByIdUseCase
import com.dailyapps.domain.usecase.GetActivityPlansByUserIdByDateUseCase
import com.dailyapps.domain.usecase.MasterUseCase
import com.dailyapps.domain.usecase.UpdateActivityPlanUseCase
import com.dailyapps.domain.usecase.UserUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.feature.activty_plan.state.ActivityPlanAction
import com.dailyapps.feature.activty_plan.state.ActivityPlanState
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
class ActivityPlanViewModel @Inject constructor(
    private val getActivityPlansByUserIdByDate: GetActivityPlansByUserIdByDateUseCase,
    private val getActivityPlanByUseCase: GetActivityPlanByIdUseCase,
    private val addActivityPlan: AddActivityPlanUseCase,
    private val updateActivityPlan: UpdateActivityPlanUseCase,
    private val addActivityPlanComment: AddActivityPlanCommentUseCase,
    private val userUseCase: UserUseCase,
    private val masterUseCase: MasterUseCase
) : ViewModelState<ActivityPlanState, ActivityPlanAction>(
    initialState = ActivityPlanState()
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
                            role = user.role ?: ""
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

    override fun handleAction(action: ActivityPlanAction) {
        when (action) {
            is ActivityPlanAction.OnGetActivityPlans -> getObservations(
                action.userId,
                action.startDate,
                action.endDate,
                action.token
            )

            is ActivityPlanAction.OnGetActivityPlan -> getActivityPlan(
                action.id,
                action.token
            )

            is ActivityPlanAction.OnUpdateDateRange -> {
                update {
                    copy(
                        list = list.copy(
                            startDate = action.startDate,
                            endDate = action.endDate
                        )
                    )
                }
            }

            is ActivityPlanAction.OnActivityPlanValueChange -> {
                update {
                    copy(
                        add = add.copy(
                            name = action.name,
                            startDate = action.startDate,
                            endDate = action.endDate,
                            lecturerId = action.lecturerId,
                        )
                    )
                }
            }

            is ActivityPlanAction.OnSubmitActivityPlan -> onSubmitObservation()

            is ActivityPlanAction.OnUpdateActivityPlan -> onUpdateObservation(action.activityPlanId)
            
            is ActivityPlanAction.OnResetState -> {
                restartState()
            }

            is ActivityPlanAction.OnSubmitReview -> onSubmitComment(action.activityPlanId, action.rating, action.comment)
        }
    }

    private fun onUpdateObservation(observationId: Long) {
        viewModelScope.launch {
            updateActivityPlan.execute(
                UpdateActivityPlanUseCase.Params(
                    observationId,
                    currentState().userId,
                    currentState().add.name,
                    currentState().add.startDate,
                    currentState().add.endDate,
                    currentState().add.lecturerId,
                    currentState().token,
                    currentState().add.status,
                )
            )
                .collectLatest { resource ->
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
                                    isError = true,
                                    errorMessage = resource.msg
                                )
                            }
                        }

                        Resource.Loading -> {
                            update {
                                copy(
                                    isLoading = true,
                                    isError = false,
                                    errorMessage = ""
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun onSubmitObservation() {
        viewModelScope.launch {
            addActivityPlan.execute(
                AddActivityPlanUseCase.Params(
                    currentState().userId,
                    currentState().add.name,
                    currentState().add.startDate,
                    currentState().add.endDate,
                    currentState().add.lecturerId,
                    currentState().token
                )
            )
                .collectLatest { resource ->
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
                                    isError = true,
                                    errorMessage = resource.msg
                                )
                            }
                        }

                        Resource.Loading -> {
                            update {
                                copy(
                                    isLoading = true,
                                    isError = false,
                                    errorMessage = ""
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun onSubmitComment(id: Long, rating: Int, comment: String) {
        viewModelScope.launch {
            addActivityPlanComment.execute(
                AddActivityPlanCommentUseCase.Params(
                    id,
                    rating,
                    comment,
                    currentState().token
                )
            )
                .collectLatest { resource ->
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
                                    isError = true,
                                    errorMessage = resource.msg
                                )
                            }
                        }

                        Resource.Loading -> {
                            update {
                                copy(
                                    isLoading = true,
                                    isError = false,
                                    errorMessage = ""
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun getObservations(
        userId: Long,
        startDate: String,
        endDate: String,
        token: String
    ) {
        viewModelScope.launch {
            getActivityPlansByUserIdByDate.execute(
                GetActivityPlansByUserIdByDateUseCase.Params(
                    userId,
                    startDate,
                    endDate,
                    false,
                    token
                )
            )
                .collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            update {
                                copy(
                                    isLoading = false,
                                    list = list.copy(
                                        activityPlans = resource.data
                                    ),
                                    isSuccess = true
                                )
                            }
                        }

                        is Resource.Error -> {
                            update {
                                copy(
                                    isLoading = false,
                                    isError = true,
                                    errorMessage = resource.msg
                                )
                            }
                        }

                        Resource.Loading -> {
                            update {
                                copy(
                                    isLoading = true,
                                    isError = false,
                                    errorMessage = ""
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun getActivityPlan(
        id: Long,
        token: String
    ) {
        viewModelScope.launch {
            getActivityPlanByUseCase.execute(
                GetActivityPlanByIdUseCase.Params(id, token)
            )
                .collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val plan = resource.data
                            update {
                                copy(
                                    isLoading = false,
                                    detail = detail.copy(
                                        activityPlan = plan
                                    ),
                                    add = add.copy(
                                        lecturerId = plan.activityPlanLecturer?.userId?.toLong() ?: 0,
                                    )
                                )
                            }
                        }

                        is Resource.Error -> {
                            update {
                                copy(
                                    isLoading = false,
                                    isError = true,
                                    errorMessage = resource.msg
                                )
                            }
                        }

                        Resource.Loading -> {
                            update {
                                copy(
                                    isLoading = true,
                                    isError = false,
                                    errorMessage = ""
                                )
                            }
                        }
                    }
                }
        }
    }
}
