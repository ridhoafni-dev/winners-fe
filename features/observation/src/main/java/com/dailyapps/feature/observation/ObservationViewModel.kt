package com.dailyapps.feature.observation

import androidx.lifecycle.viewModelScope
import com.dailyapps.common.utils.ViewModelState
import com.dailyapps.domain.usecase.AddObservationCommentUseCase
import com.dailyapps.domain.usecase.AddObservationUseCase
import com.dailyapps.domain.usecase.GetObservationByIdUseCase
import com.dailyapps.domain.usecase.GetObservationsByUserIdByDateUseCase
import com.dailyapps.domain.usecase.MasterUseCase
import com.dailyapps.domain.usecase.UpdateObservationUseCase
import com.dailyapps.domain.usecase.UserUseCase
import com.dailyapps.domain.utils.Resource
import com.dailyapps.feature.observation.state.ObservationAction
import com.dailyapps.feature.observation.state.ObservationState
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
class ObservationViewModel @Inject constructor(
    private val getObservationsByUserIdByDate: GetObservationsByUserIdByDateUseCase,
    private val getObservationByUseCase: GetObservationByIdUseCase,
    private val addObservation: AddObservationUseCase,
    private val updateObservation: UpdateObservationUseCase,
    private val addObservationComment: AddObservationCommentUseCase,
    private val userUseCase: UserUseCase,
    private val masterUseCase: MasterUseCase
) : ViewModelState<ObservationState, ObservationAction>(
    initialState = ObservationState()
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

    override fun handleAction(action: ObservationAction) {
        when (action) {
            is ObservationAction.OnGetObservations -> getObservations(
                    action.userId,
                    action.startDate,
                    action.endDate,
                    action.token
                )

            is ObservationAction.OnGetObservation -> getObservation(
                    action.id,
                    action.token
                )

            is ObservationAction.OnUpdateDateRange -> {
                update {
                    copy(
                        list = list.copy(
                            startDate = action.startDate,
                            endDate = action.endDate
                        )
                    )
                }
            }

            is ObservationAction.OnObservationValueChange -> {
                update {
                    copy(
                        add = add.copy(
                            name = action.name,
                            description = action.description,
                            date = action.date,
                            lecturerId = action.lecturerId,
                            image = action.imageUri ?: ""
                        )
                    )
                }
            }

            is ObservationAction.OnSubmitObservation -> onSubmitObservation()

            is ObservationAction.OnUpdateObservation -> onUpdateObservation(action.observationId)
            ObservationAction.OnResetState -> {
                restartState()
            }

            is ObservationAction.OnSubmitReview -> onSubmitComment(action.observationId, action.userId, action.rating, action.comment)
        }
    }


    private fun onSubmitComment(id: Long, userId: Long, rating: Int, comment: String) {
        viewModelScope.launch {
            addObservationComment.execute(
                AddObservationCommentUseCase.Params(
                    id,
                    userId,
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

    private fun onUpdateObservation(observationId: Long) {
        viewModelScope.launch {
            updateObservation.execute(
                UpdateObservationUseCase.Params(
                    observationId,
                    currentState().userId,
                    currentState().add.name,
                    currentState().add.description,
                    currentState().add.date,
                    currentState().add.lecturerId,
                    currentState().add.image,
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

    private fun onSubmitObservation() {
        viewModelScope.launch {
            addObservation.execute(
                AddObservationUseCase.Params(
                    currentState().userId,
                    currentState().add.name,
                    currentState().add.description,
                    currentState().add.date,
                    currentState().add.lecturerId,
                    currentState().add.image,
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
            getObservationsByUserIdByDate.execute(
                GetObservationsByUserIdByDateUseCase.Params(
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
                                        observations = resource.data
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

    private fun getObservation(
        id: Long,
        token: String
    ) {
        viewModelScope.launch {
            getObservationByUseCase.execute(
                GetObservationByIdUseCase.Params(id, token)
            )
                .collectLatest { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val observation = resource.data
                            update {
                                copy(
                                    isLoading = false,
                                    detail = detail.copy(
                                        observation = observation
                                    ),
                                    add = add.copy(
                                        lecturerId = observation.observationLecturer?.userId ?: 0,
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
